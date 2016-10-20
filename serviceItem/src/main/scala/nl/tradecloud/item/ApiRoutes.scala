package nl.tradecloud.item

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import nl.tradecloud.common.utils.session.{SessionManager, SessionDirectives}
import nl.tradecloud.common.views
import nl.tradecloud.common.views.Identity
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.ext.EnumNameSerializer
import org.json4s.{DefaultFormats, Formats, jackson}

import scala.language.postfixOps

trait ApiRoutes extends SessionDirectives[Identity] with Json4sSupport {
  this: Api =>

  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats + new EnumNameSerializer(views.IdentityRole)
  val sessionManager = new SessionManager[views.Identity](context.system.settings.config)

  val host = context.system.settings.config.getString("constructr.consul.agent-name")

  val routes: Route =
    pathPrefix("api" / "v1") {
      pathSingleSlash {
        get {
          complete("Welcome at the item api at: " + host)
        }
      } ~
      pathPrefix("item") {
        requireSession { identity: Identity =>
          get {
            complete(identity)
          }
        }
      }
    }
}
