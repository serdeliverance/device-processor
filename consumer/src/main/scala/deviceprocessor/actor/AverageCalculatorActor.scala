package deviceprocessor.actor

import akka.actor.typed.Behavior
import deviceprocessor.domain._
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors

import java.util.UUID
import deviceprocessor.utils.ListUtils._
import deviceprocessor.actor.MetricsAsker._

object AverageCalculatorActor {

  // protocol
  sealed trait Command
  case class Process(deviceReading: DeviceReading)                 extends Command
  case class GetAverage(replyTo: ActorRef[ReceiveAverageReadings]) extends Command
  case object Flush                                                extends Command

  // stream specific protocol
  case object StreamCompleted            extends Command
  case class StreamFailed(ex: Throwable) extends Command

  // behavior
  // TODO refactor... use TimeBoundedQueue insted of Seq
  def apply(readings: Map[UUID, Seq[DeviceReading]] = Map()): Behavior[Command] = Behaviors.receive { (context, msg) =>
    msg match {
      case Process(deviceReading) =>
        context.log.debug(s"Storing reading for average processing: $deviceReading")
        val device = readings.get(deviceReading.deviceId)
        val updatedReadings = device match {
          case Some(readings) => readings :+ deviceReading
          case None           => Seq(deviceReading)
        }
        val updatedEntry = deviceReading.deviceId -> updatedReadings
        AverageCalculatorActor(readings + updatedEntry)
      case GetAverage(replyTo) =>
        val result =
          readings.toList.map(
            entry => AverageReading(entry._1, average(entry._2.map(_.currentValue).toList))
          )
        context.log.debug(s"Average readings: $result")
        replyTo ! ReceiveAverageReadings(result)
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
