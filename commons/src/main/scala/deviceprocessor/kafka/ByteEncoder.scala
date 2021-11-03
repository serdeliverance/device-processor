package deviceprocessor.kafka

import deviceprocessor.DeviceReading
import io.circe.syntax._
import deviceprocessor.json.JsonParsing._
import ByteEncoder._
import io.circe.Encoder

object ByteEncoder {

  trait ByteEncoder[T] {
    def encode(t: T): Array[Byte]
  }

  def convertToBytes[T: ByteEncoder](t: T): Array[Byte] =
    implicitly[ByteEncoder[T]].encode(t)

  def instance[T: Encoder](): ByteEncoder[T] = new ByteEncoder[T] {
    override def encode(t: T): Array[Byte] =
      t.asJson.noSpaces.toString.getBytes
  }
}

object ByteEncoderInstance {

  implicit val deviceReadingByteEncoder: ByteEncoder[DeviceReading] = instance[DeviceReading]()
}
