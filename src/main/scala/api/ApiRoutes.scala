package api

import akka.cluster.sharding.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.util.Timeout
import commands.CreateUser
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import models.User
import org.json4s.{native, DefaultFormats}
import queries.FindUser
import responses.{NotFound, Acknowledge}
import services.UserProcessor
import akka.pattern._
import scala.concurrent.duration._
import scala.language.postfixOps

trait ApiRoutes extends Json4sSupport {
  this: Api =>
  private[this] implicit val formats = DefaultFormats
  private[this] implicit val serialization = native.Serialization
  private[this] implicit val timeout = Timeout(3 seconds)

  val host = context.system.settings.config.getString("constructr.consul.agent-name")
  def userProcessor = ClusterSharding(context.system).shardRegion(UserProcessor.shardName)

  val routes: Route =
    pathSingleSlash {
      get {
        complete("Welcome at the API of: " + host)
      }
    } ~
    pathPrefix("user") {
      pathEnd {
        post {
          entity(as[CreateUser]) { cmd =>
            onSuccess(userProcessor ? cmd) {
              case ack: Acknowledge => complete(StatusCodes.Created -> "Created user")
              case _ => complete(StatusCodes.Conflict -> s"Failed to create user, email=${cmd.email}")
            }
          }
        }
      } ~
      path(Segment) { email =>
        onSuccess(userProcessor ? FindUser(email)) {
          case usr: User => complete(usr)
          case nf: NotFound => complete(StatusCodes.NotFound -> s"User with email=$email not found")
          case _ => complete(StatusCodes.BadRequest -> "Something went wrong")
        }
      }
    }

}
