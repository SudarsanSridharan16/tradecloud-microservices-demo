package nl.tradecloud.identity

import akka.cluster.sharding.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import akka.pattern._
import akka.util.Timeout
import nl.tradecloud.common._
import nl.tradecloud.common.commands.CreateIdentity
import nl.tradecloud.common.responses.Acknowledge
import nl.tradecloud.common.utils.session.{ServerSessionDirectives, SessionManager}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import nl.tradecloud.identity.models.Identity
import nl.tradecloud.identity.queries.FindIdentity
import nl.tradecloud.identity.repositories.IdentityRepository
import org.json4s.ext.EnumNameSerializer
import org.json4s.{DefaultFormats, Formats, jackson}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait ApiRoutes extends ServerSessionDirectives[views.Identity] with Json4sSupport {
  this: Api =>
  import context.dispatcher

  implicit val formats: Formats = DefaultFormats + new EnumNameSerializer(views.IdentityRole)
  implicit val serialization = jackson.Serialization
  val sessionManager = new SessionManager[views.Identity](context.system.settings.config)
  implicit val timeout = Timeout(3 seconds)

  val host = context.system.settings.config.getString("constructr.consul.agent-name")
  def identityProcessor = ClusterSharding(context.system).shardRegion(IdentityRepository.shardName)

  def userPassAuthenticator(credentials: Credentials): Future[Option[views.Identity]] =
    credentials match {
      case password @ Credentials.Provided(id) =>
        (identityProcessor ? FindIdentity(id)).map {
          case id: Identity if password.verify(id.password, plain => BCrypt.hashpw(plain, id.salt)) =>
            Some(
              views.Identity(
                email = id.email,
                roles = id.roles.map(s => views.IdentityRole.withName(s.toString))
              )
            )
          case _ => None
        }
      case _ =>
        Future.successful(None)
    }

  val routes: Route =
    pathPrefix("api" / "v1") {
      pathSingleSlash {
        get {
          complete("Welcome at the identity API at: " + host)
        }
      } ~
      path("token") {
        get {
          authenticateBasicAsync(realm = "secure", userPassAuthenticator) { identity =>
            setSession(identity) { ctx =>
              ctx.complete("Ok")
            }
          }
        }
      } ~
      pathPrefix("identity") {
        pathEnd {
          post {
            entity(as[CreateIdentity]) { cmd =>
              onSuccess(identityProcessor ? cmd) {
                case _: Acknowledge => complete(StatusCodes.Created -> "Created identity")
                case _ => complete(StatusCodes.Conflict -> s"Failed to create identity, email=${cmd.email}")
              }
            }
          } ~
          get {
            requireSession { identity =>
              complete(identity)
            }
          }
        }
      }
    }

}
