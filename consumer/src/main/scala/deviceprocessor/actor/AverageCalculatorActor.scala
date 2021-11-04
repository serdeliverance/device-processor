package deviceprocessor.actor

import akka.actor.typed.Behavior
import deviceprocessor.domain._
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import java.util.UUID
import deviceprocessor.utils.ListUtils._

object AverageCalculatorActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading)          extends Command
  case class GetAverage(replyTo: ActorRef[AverageReadings]) extends Command
  case object Flush                                         extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  case class AverageReadings(result: List[AverageReading])

  // behavior
  def apply(readings: Map[UUID, Seq[DeviceReading]] = Map()): Behavior[Command] = Behaviors.receive { (context, msg) =>
    msg match {
      case Process(deviceReading) =>
        context.log.info(s"Storing reading: $deviceReading")
        val device = readings.get(deviceReading.deviceId)
        val updatedReadings = device match {
          case Some(readings) => readings :+ deviceReading
          case None           => Seq(deviceReading)
        }
        val updatedEntry = (deviceReading.deviceId -> updatedReadings)
        AverageCalculatorActor(readings + updatedEntry)
      case GetAverage(replyTo) =>
        val result =
          readings.toList.map(entry => AverageReading(entry._1, average(entry._2.map(_.currentValue).toList)))
        replyTo ! AverageReadings(result)
        Behaviors.same
      case Flush =>
        context.log.info(s"Flushing stored elements")
        AverageCalculatorActor()
      case StreamCompleted =>
        context.log.info(s"Stream completed")
        Behaviors.same
      case StreamFailed(ex) =>
        context.log.info(s"Error during stream processing. Ex: ${ex.getMessage}")
        Behaviors.same
    }
  }
}
