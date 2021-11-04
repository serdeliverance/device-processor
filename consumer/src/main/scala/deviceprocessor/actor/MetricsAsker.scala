package deviceprocessor.actor

import _root_.deviceprocessor.domain.AverageReading
import _root_.deviceprocessor.domain.LastReading
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.stream.scaladsl.Source
import akka.Done
import scala.concurrent.duration._
import akka.stream.scaladsl.Sink
import deviceprocessor.utils.FormatUtils._

object MetricsAsker {

  trait Command
  case class ReceiveAverageReadings(result: List[AverageReading]) extends Command
  case class ReceiveLastReadings(result: List[LastReading])       extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (context, command) =>
    command match {
      case ReceiveAverageReadings(result) =>
        context.log.info(formatAverageReadings(result))
        Behaviors.same
      case ReceiveLastReadings(result) =>
        context.log.info(formatLastReadings(result))
        Behaviors.same
    }
  }

  def metricsReader(
    metricsAsker: ActorRef[MetricsAsker.Command],
    averageCalculator: ActorRef[AverageCalculatorActor.Command],
    lastReadingTracker: ActorRef[LastReadingTrackerActor.Command]
  )(implicit system: ActorSystem[_]) =
    Source
      .tick(initialDelay = 0.seconds, interval = 1.minutes, tick = Done)
      .map { _ =>
        {
          averageCalculator ! AverageCalculatorActor.GetAverage(metricsAsker)
          lastReadingTracker ! LastReadingTrackerActor.GetLastReads(metricsAsker)
        }
      }
      .to(Sink.ignore)
}
