package deviceprocessor.kafka

import java.util.Properties

import akka.Done
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.{Future, Promise}

import ByteEncoder._

class MessageProducer(configuration: MessageProducerConfiguration) {

  private val producer = {
    val props               = new Properties
    val stringSerializer    = classOf[StringSerializer].getCanonicalName
    val byteArraySerializer = classOf[ByteArraySerializer].getCanonicalName

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.bootstrapServers)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, Unique.cleanUniqueId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, stringSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, byteArraySerializer)

    new KafkaProducer[String, Array[Byte]](props)
  }

  def send[T: ByteEncoder](
    message: T,
    topic: String,
    maybeKey: Option[String] = None
  ): Future[Done] = {
    val serializedMessage = convertToBytes(message)
    val record            = new ProducerRecord[String, Array[Byte]](topic, maybeKey.orNull, serializedMessage)
    val promise           = Promise[Done]

    producer.send(
      record,
      new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit =
          Option(exception).map(promise.failure).getOrElse(promise.success(Done))
      }
    )

    promise.future
  }

  def close(): Unit = producer.close()
}
