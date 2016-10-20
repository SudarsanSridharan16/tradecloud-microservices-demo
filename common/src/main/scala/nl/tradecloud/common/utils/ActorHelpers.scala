package nl.tradecloud.common.utils

import akka.actor.{Actor, ActorLogging, PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.{SaveSnapshotFailure, SaveSnapshotSuccess}

trait ActorHelpers extends ActorLogging {
  this: Actor =>

  protected def utils(receive: Receive): Receive = receive.orElse {
    case evt: SaveSnapshotSuccess =>
      log.debug("Snapshot success!")
    case evt: SaveSnapshotFailure =>
      log.error("Snapshot failure!")
    case ReceiveTimeout =>
      log.debug("{} received timeout, passivating...", self.path.name)
      context.parent ! Passivate(stopMessage = PoisonPill)
    case Passivate(stopMessage) =>
      sender ! stopMessage
  }

  protected def unknownMessage: Receive = {
    case msg: Any =>
      log.warning("{} received unknown message {}", self.path.name, msg)
  }
}