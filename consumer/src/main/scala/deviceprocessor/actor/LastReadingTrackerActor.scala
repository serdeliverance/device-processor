package deviceprocessor.actor

import deviceprocessor.domain.DeviceReading
import java.util.UUID
import akka.actor.typed.Behavior
import deviceprocessor.domain.LastReading
import akka.actor.typed.scaladsl.Behaviors

object LastReadingTrackerActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading) extends Command
  case object GetLastValueReport                   extends Command
  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  // behavior
  def apply(lastReadings: Map[UUID, LastReading] = Map()): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match {
        case Process(deviceReading) =>
        case GetLastValueReport     =>
        case StreamCompleted        =>
        case StreamFailed(ex)       =>
      }
    }
}
