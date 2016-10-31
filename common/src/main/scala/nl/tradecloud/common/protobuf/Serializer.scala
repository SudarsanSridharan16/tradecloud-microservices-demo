package nl.tradecloud.common.protobuf

import akka.serialization.SerializerWithStringManifest
import nl.tradecloud.common.events.{IdentityCreated, UserCreated}
import nl.tradecloud.common.protobuf.Events.UserCreatedEvt
import nl.tradecloud.common.protobuf.Events.IdentityCreatedEvt

class Serializer extends SerializerWithStringManifest {
  import CommonProtobufTransformers._

  def identifier = 20

  def manifest(obj: AnyRef): String =
    obj match {
      case _: UserCreated => UserCreated.serializeId
      case _: IdentityCreated => IdentityCreated.serializeId
    }

  def toBinary(obj: AnyRef): Array[Byte] =
    obj match {
      case msg: UserCreated => toProtobuf(msg).toByteArray
      case msg: IdentityCreated => toProtobuf(msg).toByteArray
    }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case UserCreated.serializeId => fromProtobuf(UserCreatedEvt.parseFrom(bytes))
      case IdentityCreated.serializeId => fromProtobuf(IdentityCreatedEvt.parseFrom(bytes))
    }
}
