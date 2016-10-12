import akka.actor._
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import api.Api
import com.typesafe.config.ConfigFactory
import config.AppConfig
import services.UserProcessor

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Main extends App {

  private final class Root extends Actor with ActorLogging {
    log.info("App up and running...")

    val appConfig: AppConfig = context.system.settings.config.as[AppConfig]("app")

    override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

    ClusterSharding(context.system).start(
      typeName = UserProcessor.shardName,
      entityProps = Props[UserProcessor],
      settings = ClusterShardingSettings(context.system),
      extractEntityId = UserProcessor.idExtractor,
      extractShardId = UserProcessor.shardResolver(appConfig.maxShards)
    )

    private val api = context.actorOf(Api.props(), name = Api.name)
    context.watch(api)

    override def receive = {
      case Terminated(actor) =>
        log.error("Terminating the system because {} terminated!", actor.path)
        context.system.terminate()
    }

  }

  override def main(args: Array[String]): Unit = {
    // read config
    val config = ConfigFactory.load()
    implicit val system = ActorSystem("application", config)
    Cluster(system).registerOnMemberUp(system.actorOf(Props(classOf[Root]), "root"))
    Await.ready(system.whenTerminated, Duration.Inf)
  }

}