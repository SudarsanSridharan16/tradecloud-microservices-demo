package nl.tradecloud.common.events

import nl.tradecloud.common.views.IdentityRole

case class IdentityCreated(
    email: String,
    password: String,
    salt: String,
    roles: Vector[IdentityRole.Value]
)

object IdentityCreated {
  final val publishTopic: String = "identity-created"
  final val serializeId: String = "576f8e89-1da5-4f49-9a20-a62b818bfc76"
}
