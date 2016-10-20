package nl.tradecloud.common.utils.session

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}

trait SessionDirectives[T <: AnyRef] {
  val sessionManager: SessionManager[T]

  protected[this] def requireSession: Directive1[T] = {
    optionalHeaderValueByName(sessionManager.headerName).flatMap {
      case Some(token: String) =>
        sessionManager.decode(token)
          .map(provide)
          .getOrElse(reject(AuthorizationFailedRejection))
      case _ =>
        reject(AuthorizationFailedRejection)
    }
  }

}
