package deviceprocessor

import akka.actor.typed.Behavior

import deviceprocessor.DeviceActor._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Source
import scala.concurrent.duration._
import akka.stream.typed.scaladsl.ActorSink
import com.typesafe.config.ConfigFactory
import deviceprocessor.kafka.MessageProducer
import deviceprocessor.kafka.MessageProducerConfiguration
import akka.stream.ActorMaterializer
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps

object Producer {

  def apply(deviceCount: Int, messageProducer: MessageProducer, topic: String): Behavior[Command] =
    Behaviors.setup { context =>
      context.log.info("Creating device hub")
      val deviceHub = context.spawn(DeviceHub(deviceCount, messageProducer, topic), "DeviceHub")

      Behaviors.receiveMessage { message =>
        deviceHub ! message
        Behaviors.same
      }
    }

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()

    val messageProducerConfiguration = MessageProducerConfiguration(config)

    val messageProducer = new MessageProducer(messageProducerConfiguration)
    val deviceCount     = config.getInt("device-count")
    val topic           = config.getString("topic")

    implicit val system = ActorSystem(Producer(deviceCount, messageProducer, topic), "Producer")

    val result = Source
      .tick(initialDelay = 0.seconds, interval = 3.seconds, PublishRead)
      .runWith(ActorSink.actorRef(system, StreamCompleted, ex => StreamFailed(ex)))
  }
}
