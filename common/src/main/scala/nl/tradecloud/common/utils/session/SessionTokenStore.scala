package nl.tradecloud.common.utils.session

import scala.concurrent.Future

trait SessionTokenStore[T <: AnyRef] {

  def store(token: String, sessionData: T): Future[Boolean]

  def get(token: String): Future[Option[T]]

  def remove(token: String): Future[Boolean]

}
