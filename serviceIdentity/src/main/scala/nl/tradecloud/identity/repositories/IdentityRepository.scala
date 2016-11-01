package nl.tradecloud.identity.repositories

import java.util.concurrent.TimeUnit

import akka.actor.ActorLogging
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import nl.tradecloud.common.commands.CreateIdentity
import nl.tradecloud.common.events.IdentityCreated
import nl.tradecloud.common.responses.{Acknowledge, Failure, NotFound}
import nl.tradecloud.common.utils.ActorHelpers
import nl.tradecloud.identity.models.{Identity, IdentityRole}
import nl.tradecloud.identity.queries.FindIdentity
import nl.tradecloud.kafka.KafkaExtension
import nl.tradecloud.kafka.command.Publish
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.duration.FiniteDuration

class IdentityRepository extends PersistentActor with ActorLogging with ActorHelpers {
  log.info("Started IdentityRepository, email: {}", self.path.name)

  final val persistenceId: String = "identity-" + self.path.name

  private[this] var state: Option[Identity] = None

  context.setReceiveTimeout(FiniteDuration(2, TimeUnit.MINUTES))

  def mediator = KafkaExtension(context.system).mediator

  def receiveCommand: Receive = initializing

  def initializing: Receive = utils {
    case cmd: CreateIdentity =>
      val salt = BCrypt.gensalt()
      val password = BCrypt.hashpw(cmd.plainPassword, salt)

      persist(
        event = IdentityCreated(
          email = cmd.email,
          password = password,
          salt = salt,
          roles = cmd.roles
        )
      ) { persistedEvent =>
        update(persistedEvent)
        context.become(utils(running))
        mediator ! Publish(IdentityCreated.publishTopic, persistedEvent)

        sender() ! Acknowledge()
      }
    case cmd: FindIdentity => sender() ! NotFound()
  }

  def running: Receive = utils {
    case cmd: CreateIdentity => sender() ! Failure()
    case qry: FindIdentity => sender() ! state.get
  }

  def receiveRecover: Receive = update.orElse {
    case SnapshotOffer(_, snapshot: Identity) =>
      log.info("receiveRecover=SnapshotOffer, persistenceId={}", persistenceId)
      state = Some(snapshot)
    case RecoveryCompleted =>
      log.debug("receiveRecover=RecoveryCompleted, restored state={}", state)
      context.become(state.map(_ => running).getOrElse(initializing))
  }

  private[this] def update: Receive = {
    case evt: IdentityCreated =>
      state = Some(
        Identity(
          email = evt.email,
          password = evt.password,
          salt = evt.salt,
          roles = evt.roles.map(s => IdentityRole.withName(s.toString))
        )
      )
  }
}

object IdentityRepository {

  final val shardName: String = "identity"

  final val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: CreateIdentity => (cmd.email, cmd)
    case cmd: FindIdentity => (cmd.email, cmd)
  }

  def shardResolver(maxShards: Int): ShardRegion.ExtractShardId = {
    case cmd: CreateIdentity => (math.abs(cmd.email.hashCode) % maxShards).toString
    case cmd: FindIdentity => (math.abs(cmd.email.hashCode) % maxShards).toString
  }

}
