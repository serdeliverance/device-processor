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

object DataSubscriber {

  def createSubscriberGraph(
    consumerSettings: ConsumerSettings[String, String],
    subscription: Subscription
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

        val databaseSink = Slick.sink[DeviceReading](deviceReading => deviceReadingTable += deviceReading)

        val lastValueTrackerSink = Sink.ignore

        val averageCalculatorSink = Sink.ignore

        source ~> preprocessing ~> broadcast ~> databaseSink
        broadcast ~> lastValueTrackerSink
        broadcast ~> averageCalculatorSink

        ClosedShape
      }
    )
}
