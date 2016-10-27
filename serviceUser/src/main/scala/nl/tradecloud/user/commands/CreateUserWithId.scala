package nl.tradecloud.user.commands

import java.util.UUID

import nl.tradecloud.common.commands.CreateUser

case class CreateUserWithId(
    id: UUID,
    cmd: CreateUser
)
