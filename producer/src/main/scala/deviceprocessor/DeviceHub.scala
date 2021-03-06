package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import deviceprocessor.kafka.MessageProducer
import java.util.UUID
import java.time.Instant
import deviceprocessor.DeviceActor._
import deviceprocessor.domain._

object DeviceHub {

  def apply(deviceCount: Int, messageProducer: MessageProducer, topic: String): Behavior[Command] =
    Behaviors.setup { context =>
      context.log.info(s"Setting up $deviceCount devices")
      val deviceActorList = (1 to deviceCount)
        .map(i => (i, Device(UUID.randomUUID(), s"device-$i", Instant.now())))
        .map(
          indexDeviceTup =>
            context.spawn(DeviceActor(indexDeviceTup._2, messageProducer, topic), s"DeviceActor-${indexDeviceTup._1}")
        )

      Behaviors.receive { (_, message) =>
        deviceActorList.foreach { deviceActor =>
          deviceActor ! message
        }
        Behaviors.same
      }
    }
}
