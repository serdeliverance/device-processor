package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import deviceprocessor.kafka.MessageProducer
import java.util.UUID
import java.time.Instant
import deviceprocessor.DeviceActor._

object DeviceHub {

  def apply(deviceCount: Int, messageProducer: MessageProducer, topic: String): Behavior[Command] =
    Behaviors.setup { context =>
      val deviceActorList = (1 to deviceCount)
        .map(i => (i, Device(UUID.randomUUID(), s"device-$i", Instant.now())))
        .map(
          indexDeviceTup =>
            context.spawn(DeviceActor(indexDeviceTup._2, messageProducer, topic), s"DeviceActor-${indexDeviceTup._1}")
        )

      Behaviors.receive { (context, message) =>
        deviceActorList.foreach { deviceActor =>
          deviceActor ! message
        }
        Behaviors.same
      }
    }
}
