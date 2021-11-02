package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object DeviceActor {
  // protocol
  sealed trait Command
  case object ReadDevice                               extends Command
  case class PublishRead(deviceReading: DeviceReading) extends Command

  def apply(device: Device): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      context.log.info("Device actor received")
      Behaviors.same
    }
}
