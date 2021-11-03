package deviceprocessor

import akka.actor.typed.Behavior

import deviceprocessor.DeviceActor._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Source
import scala.concurrent.duration._
import akka.stream.typed.scaladsl.ActorSink

object Producer {

  val messageProducer = ???
  val deviceCount     = ???
  val topic           = ???

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val deviceHub = context.spawn(DeviceHub(deviceCount, messageProducer, topic), "DeviceHub")

      Behaviors.receiveMessage { message =>
        deviceHub ! message
        Behaviors.same
      }
    }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[Command] = ActorSystem(Producer(), "Producer")

    Source
      .tick(initialDelay = 0.seconds, interval = 3.seconds, PublishRead)
      .to(ActorSink.actorRef(system, ???, ???))
  }
}
