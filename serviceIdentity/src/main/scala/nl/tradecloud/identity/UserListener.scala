package nl.tradecloud.identity

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.cluster.sharding.ClusterSharding
import nl.tradecloud.common.commands.CreateIdentity
import nl.tradecloud.common.events.UserCreated
import nl.tradecloud.common.utils.ActorHelpers
import nl.tradecloud.identity.repositories.IdentityRepository
import nl.tradecloud.kafka.KafkaExtension
import nl.tradecloud.kafka.command.Subscribe
import nl.tradecloud.kafka.response.{PubSubAck, SubscribeAck}

class UserListener extends Actor with ActorLogging with ActorHelpers with Stash {
  log.info("Started UserListener")

  def mediator = KafkaExtension(context.system).mediator

  mediator ! Subscribe(UserListener.name, Set(UserCreated.publishTopic), self)

  def identityProcessor = ClusterSharding(context.system).shardRegion(IdentityRepository.shardName)

  def receive: Receive = {
    case ack: SubscribeAck => log.info("Received kafka acknowledge, ack={}", ack)
    case evt: UserCreated =>
      log.info("Received event, event={}", evt)

      identityProcessor ! CreateIdentity(
        email = evt.email,
        plainPassword = evt.plainPassword,
        roles = evt.roles
      )

      sender() ! PubSubAck
  }
}

object UserListener {

  final val name: String = "user-listener"

  def props(): Props = Props(classOf[UserListener])


}
