import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

trait ApiRoutes {
  this: Actor =>
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)

  val host = context.system.settings.config.getString("constructr.consul.agent-name")

  val routes: Route =
    pathSingleSlash {
      get {
        complete("Hello AkkaDocker at: " + host)
      }
    }

}
