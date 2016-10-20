package nl.tradecloud.item

import akka.actor._
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ItemService extends App {
   private final class Root extends Actor with ActorLogging {
     log.info("ItemService up and running...")

     val appConfig: AppConfig = context.system.settings.config.as[AppConfig]("app")

     override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

     private val api = context.actorOf(Api.props(), name = Api.name)
     context.watch(api)

     override def receive = {
       case Terminated(actor) =>
         log.error("Terminating the system because {} terminated!", actor.path)
         context.system.terminate()
     }

   }

   val config = ConfigFactory.load()
   implicit val system = ActorSystem("item-service", config)
   Cluster(system).registerOnMemberUp(system.actorOf(Props(classOf[Root]), "root"))
   Await.ready(system.whenTerminated, Duration.Inf)
 }
