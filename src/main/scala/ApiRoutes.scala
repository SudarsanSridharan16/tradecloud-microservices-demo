import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

trait ApiRoutes {
  this: Actor =>
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)

  val routes: Route =
    pathSingleSlash {
      get {
        complete("Ok")
      }
    }

}
