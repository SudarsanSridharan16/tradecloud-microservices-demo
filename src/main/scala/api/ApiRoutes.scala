package api

import akka.cluster.sharding.ClusterSharding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import services.UserProcessor

trait ApiRoutes {
  this: Api =>

  val host = context.system.settings.config.getString("constructr.consul.agent-name")
  def userProcessor = ClusterSharding(context.system).shardRegion(UserProcessor.shardName)

  val routes: Route =
    pathSingleSlash {
      get {
        complete("Welcome at the API of: " + host)
      }
    }

}
