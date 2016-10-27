package nl.tradecloud.common.protobuf

import java.util.UUID

import nl.tradecloud.common.events.UserCreated
import nl.tradecloud.common.views.IdentityRole
import org.joda.time.DateTime
import nl.tradecloud.common.protobuf.Events.UserCreatedEvt

object CommonProtobufTransformers {

  def toProtobuf(msg: UserCreated): UserCreatedEvt = {
    UserCreatedEvt(
      id = Some(msg.id.toString),
      email = Some(msg.email),
      name = Some(msg.name),
      createdAt = Some(msg.createdAt.toString),
      roles = msg.roles.map(_.toString),
      plainPassword = Some(msg.plainPassword)
    )
  }

  def fromProtobuf(msg: UserCreatedEvt): UserCreated = {
    for {
      id <- msg.id.map(UUID.fromString)
      email <- msg.email
      name <- msg.name
      createdAt <- msg.createdAt.map(DateTime.parse)
      plainPassword <- msg.plainPassword
    } yield UserCreated(
      id = id,
      email = email,
      name = name,
      createdAt = createdAt,
      roles = msg.roles.map(IdentityRole.withName(_)).toVector,
      plainPassword = plainPassword
    )
  }.getOrElse(throw new RuntimeException("Unable to deserialize, msg=" + msg))

}
