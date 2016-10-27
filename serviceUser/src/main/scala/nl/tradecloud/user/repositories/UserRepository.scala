package nl.tradecloud.user.repositories

import java.util.concurrent.TimeUnit

import akka.actor.ActorLogging
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import nl.tradecloud.common.commands.CreateUser
import nl.tradecloud.common.events.UserCreated
import nl.tradecloud.common.queries.FindUserById
import nl.tradecloud.common.responses.{Acknowledge, Failure, NotFound}
import nl.tradecloud.common.utils.ActorHelpers
import nl.tradecloud.kafka.KafkaExtension
import nl.tradecloud.kafka.command.Publish
import nl.tradecloud.user.commands.CreateUserWithId
import nl.tradecloud.user.models.User
import org.joda.time.DateTime

import scala.concurrent.duration.FiniteDuration

class UserRepository extends PersistentActor with ActorLogging with ActorHelpers {
  log.info("Started UserRepository, userId={}", self.path.name)

  def mediator = KafkaExtension(context.system).mediator

  final val persistenceId: String = "user-" + self.path.name

  private[this] var state: Option[User] = None

  context.setReceiveTimeout(FiniteDuration(2, TimeUnit.MINUTES))

  def receiveCommand: Receive = initializing

  def initializing: Receive = utils {
    case wrappedCmd: CreateUserWithId =>
      val cmd = wrappedCmd.cmd

      persist(
        event = UserCreated(
          id = wrappedCmd.id,
          name = cmd.name,
          email = cmd.email,
          plainPassword = cmd.plainPassword,
          roles = cmd.roles,
          createdAt = DateTime.now()
        )
      ) { persistedEvent =>
        update(persistedEvent)
        context.become(utils(running))

        mediator ! Publish(UserCreated.publishTopic, persistedEvent)

        sender() ! Acknowledge()
      }
    case cmd: FindUserById => sender() ! NotFound()
  }

  def running: Receive = utils {
    case cmd: CreateUser => sender() ! Failure()
    case qry: FindUserById => sender() ! state.get
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
          id = evt.id,
          email = evt.email,
          name = evt.name,
          telephone = None,
          createdAt = DateTime.now()
        )
      )
  }
}

object UserRepository {
  final val shardName: String = "user"

  final val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: CreateUserWithId => (cmd.id.toString, cmd)
    case cmd: FindUserById => (cmd.id.toString, cmd)
  }

  def shardResolver(maxShards: Int): ShardRegion.ExtractShardId = {
    case cmd: CreateUserWithId => (math.abs(cmd.id.hashCode) % maxShards).toString
    case cmd: FindUserById => (math.abs(cmd.id.hashCode) % maxShards).toString
  }

}
