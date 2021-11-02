package deviceprocessor.kafka

object ByteEncoder {

  trait ByteEncoder[T] {
    def encode(t: T): Array[Byte]
  }

  def convertToBytes[T: ByteEncoder](t: T): Array[Byte] =
    implicitly[ByteEncoder[T]].encode(t)
}
