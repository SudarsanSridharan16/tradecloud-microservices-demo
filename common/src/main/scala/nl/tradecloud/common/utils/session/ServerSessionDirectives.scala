package nl.tradecloud.common.utils.session

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._

trait ServerSessionDirectives[T <: AnyRef] extends SessionDirectives[T] {

  protected[this] def setSession(sessionData: T): Directive0 = {
    respondWithHeaders(
      RawHeader(
        name = sessionManager.setHeaderName,
        value = sessionManager.encode(sessionData)
      )
    )
  }

}
