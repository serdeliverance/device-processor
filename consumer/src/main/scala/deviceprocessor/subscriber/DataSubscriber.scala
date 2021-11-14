package deviceprocessor.subscriber

import akka.NotUsed
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.kafka.{ConsumerSettings, Subscription}
import akka.kafka.scaladsl.Consumer
import akka.stream.ClosedShape
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph}
import akka.stream.typed.scaladsl.ActorSink
import deviceprocessor.actor.{AverageCalculatorActor, LastReadingTrackerActor}
import deviceprocessor.db.DeviceReadingTable._
import deviceprocessor.domain.DeviceReading
import deviceprocessor.json.JsonParsing._
import io.circe.parser.decode
import org.apache.kafka.clients.consumer.ConsumerRecord
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._

object DataSubscriber {

  def consumerGraph(
    averageCalculator: ActorRef[AverageCalculatorActor.Command],
    lastReadingTracker: ActorRef[LastReadingTrackerActor.Command],
    consumerSettings: ConsumerSettings[String, String],
    subscription: Subscription
  )(
    implicit system: ActorSystem[_],
    slickSession: SlickSession
  ): RunnableGraph[NotUsed] =
    RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val source =
          Consumer.plainSource(consumerSettings, subscription)

        val preprocessing =
          Flow[ConsumerRecord[String, String]].map(record => decode[DeviceReading](record.value())).collect {
            case Right(deviceReading) => deviceReading
          }

        val broadcast = builder.add(Broadcast[DeviceReading](3))

        val lastValueTrackerProtocolAdapter =
          Flow[DeviceReading].map(deviceReading => LastReadingTrackerActor.Process(deviceReading))

        val readingPerMinuteGrouper =
          Flow[DeviceReading]
            .groupedWithin(Int.MaxValue, 1.minutes)
            .map(readingBatch => readingBatch.groupBy(_.deviceId))
            .map(deviceReadingMap => deviceReadingMap.view.mapValues(_.sortBy(_.timestamp).reverse.head))
            .mapConcat(lastMinuteReadingsEntry => lastMinuteReadingsEntry.toList.map(_._2))

        val averageCalculatorProtocolAdapter =
          Flow[DeviceReading].map(deviceReading => AverageCalculatorActor.Process(deviceReading))

        val databaseSink = Slick.sink[DeviceReading](deviceReading => deviceReadingTable += deviceReading)

        val lastValueTrackerSink =
          ActorSink
            .actorRef[LastReadingTrackerActor.Command](
              lastReadingTracker,
              LastReadingTrackerActor.StreamCompleted,
              ex => LastReadingTrackerActor.StreamFailed(ex)
            )

        val averageCalculatorSink =
          ActorSink
            .actorRef[AverageCalculatorActor.Command](
              averageCalculator,
              AverageCalculatorActor.StreamCompleted,
              ex => AverageCalculatorActor.StreamFailed(ex)
            )

        source ~> preprocessing ~> broadcast ~> databaseSink
        broadcast ~> lastValueTrackerProtocolAdapter ~> lastValueTrackerSink
        broadcast ~> readingPerMinuteGrouper ~> averageCalculatorProtocolAdapter ~> averageCalculatorSink

        ClosedShape
      }
    )
}
