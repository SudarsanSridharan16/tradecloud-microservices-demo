package nl.tradecloud.user.models

import java.util.UUID

import org.joda.time.DateTime

case class User(
    id: UUID,
    name: String,
    email: String,
    telephone: Option[String],
    createdAt: DateTime
)
