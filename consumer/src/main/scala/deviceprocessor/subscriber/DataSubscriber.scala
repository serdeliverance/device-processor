package deviceprocessor.subscriber

import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.GraphDSL
import akka.NotUsed
import akka.stream.scaladsl.Broadcast
import akka.Done
import akka.stream.ClosedShape
import akka.kafka.scaladsl.Consumer
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.ConsumerRecord
import akka.stream.scaladsl.Flow
import deviceprocessor.json.JsonParsing._

import io.circe.parser.decode
import io.circe.syntax._
import akka.stream.scaladsl.Sink
import akka.actor.typed.ActorSystem
import akka.stream.alpakka.slick.scaladsl.SlickSession

import deviceprocessor.Consumer._
import akka.stream.alpakka.slick.scaladsl.Slick

import slick.jdbc.PostgresProfile.api._

import deviceprocessor.db.DeviceReadingTable._
import akka.kafka.Subscription
import deviceprocessor.domain.DeviceReading
import akka.stream.typed.scaladsl.ActorSink
import deviceprocessor.actor.LastReadingTrackerActor
import akka.actor.typed.ActorRef
import deviceprocessor.actor.AverageCalculatorActor

object DataSubscriber {

  def consumerGraph(
    consumerSettings: ConsumerSettings[String, String],
    subscription: Subscription,
    averageCalculator: ActorRef[AverageCalculatorActor.Command],
    lastReadingTracker: ActorRef[LastReadingTrackerActor.Command]
  )(
    implicit system: ActorSystem[Command],
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
        broadcast ~> averageCalculatorProtocolAdapter ~> averageCalculatorSink

        ClosedShape
      }
    )
}
