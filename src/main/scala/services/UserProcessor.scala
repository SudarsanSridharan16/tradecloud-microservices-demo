package services

import akka.actor.ActorLogging
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import commands.CreateUser
import events.UserCreated
import models.User
import queries.FindUser
import responses.{Acknowledge, Failure, NotFound}

class UserProcessor extends PersistentActor with ActorLogging {

  log.info("Started UserProcessor: " + self.path.name)

  final val persistenceId: String = "user-" + self.path.name

  private[this] var state: Option[User] = None

  def receiveCommand: Receive = initializing

  def initializing: Receive = {
    case cmd: CreateUser =>
      persist(
        event = UserCreated(
          email = cmd.email,
          name = cmd.name
        )
      ) { persistedEvent =>
        update(persistedEvent)
        context.become(running)

        sender() ! Acknowledge()
      }
    case qry: FindUser => sender() ! NotFound()
  }

  def running: Receive = {
    case cmd: CreateUser => sender() ! Failure()
    case qry: FindUser => sender() ! state.get
  }

  def receiveRecover: Receive = update.orElse {
    case SnapshotOffer(_, snapshot: User) =>
      log.info("receiveRecover=SnapshotOffer, persistenceId={}", persistenceId)
      state = Some(snapshot)
    case RecoveryCompleted =>
      log.debug("receiveRecover=RecoveryCompleted, restored state={}", state)
      context.become(state.map(_ => running).getOrElse(initializing))
  }

  private[this] def update: Receive = {
    case evt: UserCreated =>
      state = Some(
        User(
          email = evt.email,
          name = evt.name
        )
      )
  }
}

object UserProcessor {

  final val shardName: String = "user"

  final val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: CreateUser => (cmd.email, cmd)
    case cmd: FindUser => (cmd.email, cmd)
  }

  def shardResolver(maxShards: Int): ShardRegion.ExtractShardId = {
    case cmd: CreateUser => (math.abs(cmd.email.hashCode) % maxShards).toString
    case cmd: FindUser => (math.abs(cmd.email.hashCode) % maxShards).toString
  }

}
