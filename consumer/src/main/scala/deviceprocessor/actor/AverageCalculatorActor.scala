package deviceprocessor.actor

import akka.actor.typed.Behavior
import deviceprocessor.domain._
import akka.actor.typed.ActorRef
import deviceprocessor.Consumer.Command

object AverageCalculatorActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading)          extends Command
  case class GetAverage(replyTo: ActorRef[AverageReadings]) extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  case class AverageReadings(result: List[AverageReading])

  // behavior
  def apply(): Behavior[Command] = ???
}
