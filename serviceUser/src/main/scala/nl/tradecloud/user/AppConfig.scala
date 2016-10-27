package nl.tradecloud.user

case class AppConfig(
    numberOfNodes: Int
) {
  final val maxShards: Int = numberOfNodes * 10
}