package nl.tradecloud.common.views

case class Identity(
    email: String,
    roles: Vector[IdentityRole.Value]
)

object IdentityRole extends Enumeration {
  val Admin = Value("admin")
  val SuperAdmin = Value("super_admin")
  val User = Value("user")
}