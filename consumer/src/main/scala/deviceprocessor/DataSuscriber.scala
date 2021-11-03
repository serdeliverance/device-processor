package deviceprocessor

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

object DataSuscriber {

  def createSubscriberGraph(consumerSettings: ConsumerSettings[String, String], topic: String): RunnableGraph[NotUsed] =
    RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val source =
          Consumer.plainSource(consumerSettings, Subscriptions.assignment(new TopicPartition(topic, 0)))

        val preprocessing = Flow[ConsumerRecord[String, String]].map(record => decode[DeviceReading](record.value()))

        // val broadcast = builder.add(Broadcast[Done](3))

        val testSink = Sink.foreach(println)

        source ~> preprocessing ~> testSink

        ClosedShape
      }
    )
}
