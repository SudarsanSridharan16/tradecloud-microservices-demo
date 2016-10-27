package nl.tradecloud.common.events

import java.util.UUID

import nl.tradecloud.common.views.IdentityRole
import org.joda.time.DateTime

case class UserCreated(
    id: UUID,
    name: String,
    email: String,
    plainPassword: String,
    roles: Vector[IdentityRole.Value],
    createdAt: DateTime
)

object UserCreated {
  final val publishTopic: String = "user-created"
}
