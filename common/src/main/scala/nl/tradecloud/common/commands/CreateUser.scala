package nl.tradecloud.common.commands

import nl.tradecloud.common.views.IdentityRole

case class CreateUser(
    name: String,
    email: String,
    plainPassword: String,
    roles: Vector[IdentityRole.Value]
)