package nl.tradecloud.user

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import nl.tradecloud.user.repositories.UserRepository

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object UserService extends App {
   private final class Root extends Actor with ActorLogging {
     log.info("UserService up and running...")

     val appConfig: AppConfig = context.system.settings.config.as[AppConfig]("app")

     override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

     ClusterSharding(context.system).start(
       typeName = UserRepository.shardName,
       entityProps = Props[UserRepository],
       settings = ClusterShardingSettings(context.system),
       extractEntityId = UserRepository.idExtractor,
       extractShardId = UserRepository.shardResolver(appConfig.maxShards)
     )

     context.watch(context.actorOf(Api.props(), name = Api.name))

     override def receive = {
       case Terminated(actor) =>
         log.error("Terminating the system because {} terminated!", actor.path)
         context.system.terminate()
     }

   }

   val config = ConfigFactory.load()
   implicit val system = ActorSystem("user-service", config)
   Cluster(system).registerOnMemberUp(system.actorOf(Props(classOf[Root]), "root"))
   Await.ready(system.whenTerminated, Duration.Inf)
 }
