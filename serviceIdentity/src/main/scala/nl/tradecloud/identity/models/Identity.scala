package nl.tradecloud.identity.models

case class Identity(
    email: String,
    password: String,
    salt: String,
    roles: Vector[IdentityRole.Value]
)

object IdentityRole extends Enumeration {
  val Admin = Value("admin")
  val SuperAdmin = Value("super_admin")
  val User = Value("user")
}