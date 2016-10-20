import nl.tradecloud.common.views
import nl.tradecloud.common.views.{IdentityRole, Identity}
import org.json4s.DefaultFormats
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.Serialization
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
implicit val formats = DefaultFormats + new EnumNameSerializer(views.IdentityRole)

val secret = "tha-secret"

val identity = Identity(
  email = "test@test.nl",
  roles = Vector(IdentityRole.Admin)
)
val token = Jwt.encode(
  JwtClaim(
    content = Serialization.write(identity)
  ),
  secret,
  JwtAlgorithm.HS256
)


val stringClaim = Jwt.decodeRawAll(
  token,
  secret,
  Seq(JwtAlgorithm.HS256)
).get

val claim = stringClaim._2

