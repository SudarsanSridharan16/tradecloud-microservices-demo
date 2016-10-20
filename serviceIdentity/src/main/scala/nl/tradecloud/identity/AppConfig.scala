package nl.tradecloud.identity

case class AppConfig(
    numberOfNodes: Int
) {
  final val maxShards: Int = numberOfNodes * 10
}