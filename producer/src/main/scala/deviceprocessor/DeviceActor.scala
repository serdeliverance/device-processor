package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import deviceprocessor.kafka.MessageProducer
import java.time.Instant
import deviceprocessor.kafka.ByteEncoderInstance._
import akka.actor.typed.ActorRef
import scala.util.Random
import deviceprocessor.domain._

object DeviceActor {

  // protocol
  sealed trait Command
  case object PublishRead extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  private val LOW_MEASSURE = -10
  private val MAX_MEASSURE = 30

  def apply(device: Device, messageProducer: MessageProducer, topic: String): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case PublishRead =>
          val deviceReading =
            DeviceReading(device.deviceId, Random.between(LOW_MEASSURE, MAX_MEASSURE), "farenheit", Instant.now, 1)
          context.log.info(
            s"deviceId=${device.deviceId} name=${device.name} publishing read: value=${deviceReading.currentValue} unit=${deviceReading.unit} timestamp=${deviceReading.timestamp}"
          )
          messageProducer.send(deviceReading, topic)
          Behaviors.same
        case StreamCompleted =>
          context.log.info("Stream completed")
          Behaviors.same
        case StreamFailed(ex) =>
          context.log.info("Stream failed during processing")
          Behaviors.same
      }
    }
}
