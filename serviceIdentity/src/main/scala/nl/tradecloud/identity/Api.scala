package nl.tradecloud.identity

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

class Api() extends Actor with ActorLogging with ApiRoutes {
  import context.dispatcher
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  log.info("Identity api up and running...")

  val binding: Future[ServerBinding] = Http().bindAndHandle(routes, "0.0.0.0", 8080)

  binding.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}!", "0.0.0.0", 8080)
  }

  override def postStop(): Unit = {
    log.info("Stopping api...")
    Await.result(binding.map(_.unbind()), FiniteDuration(30, TimeUnit.SECONDS))
  }

  def receive: Receive = LoggingReceive {
    case a => log.warning("Unknown message")
  }
}

object Api {
  final val name: String = "api"

  def props(): Props = {
    Props(
      classOf[Api]
    )
  }

}
