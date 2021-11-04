package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.ActorSystem
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.Subscriptions
import org.apache.kafka.common.TopicPartition
import com.typesafe.config.ConfigFactory

import deviceprocessor.subscriber.DataSubscriber._
import akka.kafka.ConsumerSettings
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import akka.kafka.Subscription
import deviceprocessor.actor.AverageCalculatorActor
import deviceprocessor.actor.LastReadingTrackerActor
import akka.actor.typed.SpawnProtocol
import akka.actor.typed.Props
import akka.actor.typed.scaladsl.AskPattern._
import akka.Done
import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.util.Timeout
import scala.concurrent.duration._
import deviceprocessor.actor.MetricsAsker._
import deviceprocessor.actor.MetricsAsker

object Consumer {

  def main(args: Array[String]): Unit = {

    val config              = ConfigFactory.load()
    val kafkaConsumerConfig = config.getConfig("akka.kafka.consumer")

    val consumerSettings = ConsumerSettings(kafkaConsumerConfig, new StringDeserializer, new StringDeserializer)
    val topic            = config.getString("topic")
    val subscription     = Subscriptions.assignment(new TopicPartition(topic, 0))

    val metricsPollInterval = config.getDuration("metrics-poll-interval")

    val initialBehavior: Behavior[SpawnProtocol.Command] = Behaviors.setup { context =>
      SpawnProtocol()
    }

    implicit val system           = ActorSystem(initialBehavior, "Consumer")
    implicit val executionContext = system.executionContext
    implicit val slickSession     = SlickSession.forConfig("slick-postgres")

    implicit val timeout = Timeout(3.seconds)

    for {
      averageCalculator <- system.ask[ActorRef[AverageCalculatorActor.Command]] { ref =>
        SpawnProtocol.Spawn(behavior = AverageCalculatorActor(), name = "AverageCalculator", props = Props.empty, ref)
      }
      lastReadingTracker <- system.ask[ActorRef[LastReadingTrackerActor.Command]] { ref =>
        SpawnProtocol
          .Spawn(behavior = LastReadingTrackerActor(), name = "LastReadingTracker", props = Props.empty, ref)
      }
      metricsAsker <- system.ask[ActorRef[MetricsAsker.Command]] { ref =>
        SpawnProtocol
          .Spawn(behavior = MetricsAsker(), name = "MetricsAsker", props = Props.empty, ref)
      }
      _ <- Future {
        consumerGraph(averageCalculator, lastReadingTracker, consumerSettings, subscription).run()
        metricsReader(metricsAsker, averageCalculator, lastReadingTracker).run()
      }
    } yield (averageCalculator, lastReadingTracker)

  }

}
