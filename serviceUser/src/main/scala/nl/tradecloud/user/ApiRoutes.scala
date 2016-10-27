package nl.tradecloud.user

import java.util.UUID

import akka.cluster.sharding.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import nl.tradecloud.common.commands.CreateUser
import nl.tradecloud.common.responses.Acknowledge
import nl.tradecloud.common.utils.session.{SessionDirectives, SessionManager}
import nl.tradecloud.common.views
import nl.tradecloud.common.views.Identity
import nl.tradecloud.user.commands.CreateUserWithId
import nl.tradecloud.user.repositories.UserRepository
import org.json4s.ext.EnumNameSerializer
import org.json4s.{DefaultFormats, Formats, jackson}

import scala.concurrent.duration._
import scala.language.postfixOps

trait ApiRoutes extends SessionDirectives[Identity] with Json4sSupport {
  this: Api =>

  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats + new EnumNameSerializer(views.IdentityRole)
  val sessionManager = new SessionManager[views.Identity](context.system.settings.config)
  implicit val timeout = Timeout(3 seconds)

  val host = context.system.settings.config.getString("constructr.consul.agent-name")
  def userProcessor = ClusterSharding(context.system).shardRegion(UserRepository.shardName)

  val routes: Route =
    pathPrefix("api" / "v1") {
      pathSingleSlash {
        get {
          complete("Welcome at the user api at: " + host)
        }
      } ~
      pathPrefix("user") {
        requireSession { identity: Identity =>
          get {
            complete(identity)
          } ~
          post {
            entity(as[CreateUser]) { cmd =>
              val wrappedCmd = CreateUserWithId(
                id = UUID.randomUUID(),
                cmd = cmd
              )

              onSuccess(userProcessor ? wrappedCmd) {
                case _: Acknowledge => complete(StatusCodes.Created -> "Created user")
                case _ => complete(StatusCodes.Conflict -> s"Failed to create user, email=${cmd.email}")
              }
            }
          }
        }
      }
    }
}
