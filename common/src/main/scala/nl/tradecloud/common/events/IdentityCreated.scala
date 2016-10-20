package nl.tradecloud.common.events

import nl.tradecloud.common.views.IdentityRole

case class IdentityCreated(
    email: String,
    password: String,
    salt: String,
    roles: Vector[IdentityRole.Value]
)
