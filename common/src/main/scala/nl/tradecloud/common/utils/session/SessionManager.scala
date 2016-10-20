package nl.tradecloud.common.utils.session

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.json4s.Formats
import org.json4s.jackson.Serialization
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import scala.util.Try

class SessionManager[T <: AnyRef](config: Config)(implicit val formats: Formats, mf: Manifest[T]) {
  private[this] final val sessionConfig: SessionConfig = config.as[SessionConfig]("akka.http.session")
  private[this] final val algorithm: JwtHmacAlgorithm = JwtAlgorithm.HS256

  lazy val setHeaderName: String = sessionConfig.setHeaderName.getOrElse("Set-Authorization")
  lazy val headerName: String = sessionConfig.headerName.getOrElse("Authorization")

  def encode(sessionData: T): String = {
    val claim: JwtClaim = JwtClaim(
      content = Serialization.write[T](sessionData)
    )
    Jwt.encode(
      sessionConfig.headerTtl
        .map(ttl => claim.issuedNow.expiresIn(ttl))
        .getOrElse(claim),
      sessionConfig.secret,
      algorithm
    )
  }

  def decode(token: String): Try[T] = {
    Jwt.decode(
      token,
      sessionConfig.secret,
      Seq(algorithm)
    ).flatMap(t => Try(Serialization.read[T](t)))
  }

}