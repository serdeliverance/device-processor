package deviceprocessor.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import deviceprocessor.actor.MetricsAsker._
import deviceprocessor.domain._

import java.util.UUID

object LastReadingTrackerActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading)                extends Command
  case class GetLastReads(replyTo: ActorRef[ReceiveLastReadings]) extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  // behavior
  def apply(lastReadings: Map[UUID, LastReading] = Map()): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match {
        case Process(deviceReading) =>
          context.log.debug(s"Storing device reading for last reading processing: $deviceReading")
          val reading             = LastReading(deviceReading.deviceId, deviceReading.currentValue)
          val updatedLastReadings = lastReadings + (deviceReading.deviceId -> reading)
          LastReadingTrackerActor(updatedLastReadings)
        case GetLastReads(replyTo) =>
          context.log.debug(s"Last reading values: $lastReadings")
          replyTo ! ReceiveLastReadings(lastReadings.values.toList)
          Behaviors.same
        case StreamCompleted =>
          context.log.info(s"Stream completed")
          Behaviors.same
        case StreamFailed(_) =>
          context.log.info(s"Retrieving last readings: $lastReadings")
          Behaviors.same
      }
    }
}
