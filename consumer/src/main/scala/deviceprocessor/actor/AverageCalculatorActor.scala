package deviceprocessor.actor

import akka.actor.typed.Behavior
import deviceprocessor.domain._
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors

import java.util.UUID
import deviceprocessor.utils.ListUtils._
import deviceprocessor.actor.MetricsAsker._
import deviceprocessor.queue.{DQueue, TimeBoundedDQueue}

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
  def apply(readings: Map[UUID, DQueue[DeviceReading]] = Map()): Behavior[Command] = Behaviors.receive {
    (context, msg) =>
      msg match {
        case Process(deviceReading) =>
          context.log.debug(s"Storing reading for average processing: $deviceReading")
          val device = readings.get(deviceReading.deviceId)
          val accumulatedReadings = device match {
            case Some(queue) => queue.enqueue(deviceReading)
            case None        => TimeBoundedDQueue[DeviceReading]().enqueue(deviceReading)
          }
          val updatedEntry = deviceReading.deviceId -> accumulatedReadings
          AverageCalculatorActor(readings + updatedEntry)
        case GetAverage(replyTo) =>
          val result =
            readings.toList.map(entry => AverageReading(entry._1, average(entry._2.getAll.map(_.currentValue))))
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
