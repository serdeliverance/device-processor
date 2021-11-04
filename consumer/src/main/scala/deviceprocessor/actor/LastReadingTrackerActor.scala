package deviceprocessor.actor

import deviceprocessor.domain.DeviceReading
import java.util.UUID
import akka.actor.typed.Behavior
import deviceprocessor.domain.LastReading
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import deviceprocessor.Consumer._

object LastReadingTrackerActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading)         extends Command
  case class GetLastReads(replyTo: ActorRef[LastReadings]) extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  case class LastReadings(result: List[LastReading])

  // behavior
  def apply(lastReadings: Map[UUID, LastReading] = Map()): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match {
        case Process(deviceReading) =>
          context.log.info(s"Adding device reading: $deviceReading")
          val reading             = LastReading(deviceReading.deviceId, deviceReading.currentValue)
          val updatedLastReadings = lastReadings + (deviceReading.deviceId -> reading)
          LastReadingTrackerActor(updatedLastReadings)
        case GetLastReads(replyTo) =>
          context.log.info(s"Retrieving last readings: $lastReadings")
          replyTo ! LastReadings(lastReadings.values.toList)
          Behaviors.same
        case StreamCompleted =>
          context.log.info(s"Stream completed")
          Behaviors.same
        case StreamFailed(ex) =>
          context.log.info(s"Retrieving last readings: $lastReadings")
          Behaviors.same
      }
    }
}
