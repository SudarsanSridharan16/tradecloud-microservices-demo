package nl.tradecloud.common.utils.session

case class SessionConfig(
    secret: String,
    headerTtl: Option[Long],
    setHeaderName: Option[String],
    headerName: Option[String],
    refreshHeaderName: Option[String]
)
