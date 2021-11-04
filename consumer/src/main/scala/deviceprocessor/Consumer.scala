package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.ActorSystem
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.Subscriptions
import org.apache.kafka.common.TopicPartition
import com.typesafe.config.ConfigFactory

import deviceprocessor.subscriber._
import akka.kafka.ConsumerSettings
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import akka.kafka.Subscription
import deviceprocessor.actor.AverageCalculatorActor
import deviceprocessor.actor.LastReadingTrackerActor

object Consumer {

  // TODO remove
  trait Command

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    ???
  }

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()

    val kafkaConsumerConfig = config.getConfig("kafka-consumer")

    val consumerSettings = ConsumerSettings(kafkaConsumerConfig, new StringDeserializer, new ByteArrayDeserializer)
    val topic            = config.getString("topic")
    val subscription     = Subscriptions.assignment(new TopicPartition(topic, 0))

    implicit val system       = ActorSystem(Consumer(), "Consumer")
    implicit val slickSession = SlickSession.forConfig("slick-postgres")

    val consumerGraph = ???
  }

}
