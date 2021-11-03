package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import deviceprocessor.kafka.MessageProducer
import java.time.Instant
import deviceprocessor.utils.RandomUtils
import deviceprocessor.kafka.ByteEncoderInstance._
import akka.actor.typed.ActorRef

object DeviceActor extends RandomUtils {

  // protocol
  sealed trait Command
  case object PublishRead extends Command

  def apply(device: Device, messageProducer: MessageProducer, topic: String): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case PublishRead =>
          val deviceReading =
            DeviceReading(device.deviceId, generateRandomDoubleBetween(1, 2), "farenheit", Instant.now, 1)
          context.log.info(s"device: ${device.deviceId} pusblishing reading: $deviceReading")
          messageProducer.send(deviceReading, topic)
          Behaviors.same
      }
    }
}
