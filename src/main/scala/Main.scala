import java.util.concurrent.TimeUnit

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

object Main extends App {
  // read config
  val config = ConfigFactory.load()

  // initialize basics
  implicit val system = ActorSystem("application", config)

  // Graceful actor system shutdown
  sys.addShutdownHook {
    system.log.info("Terminating...")
    system.terminate()
    Await.result(system.whenTerminated, FiniteDuration(30, TimeUnit.SECONDS))
  }

//  system.actorOf(Props[ClusterListener])

  system.actorOf(Api.props(), name = Api.name)
}

class ClusterListener extends Actor with ActorLogging {

  override def preStart() = Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])

  def receive = {
    case MemberUp(member) => log.info("memberUp={}", member.address)
    case event => log.debug("event={}", event.toString)
  }
}
