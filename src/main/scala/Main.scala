import akka.actor._
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  private final class Root extends Actor with ActorLogging {

    log.info("App up and running")

    override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

    private val api = context.actorOf(Api.props(), name = Api.name)
    context.watch(api)

    override def receive = {
      case Terminated(actor) =>
        log.error(s"Terminating the system because ${ actor.path } terminated!")
        context.system.terminate()
    }

  }

  override def main(args: Array[String]): Unit = {
    // read config
    val config = ConfigFactory.load()
    implicit val system = ActorSystem("application", config)
    Cluster(system).registerOnMemberUp(system.actorOf(Props(new Root), "root"))
    Await.ready(system.whenTerminated, Duration.Inf)
  }

}