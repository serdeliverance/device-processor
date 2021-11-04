package deviceprocessor

import akka.actor.typed.Behavior
import akka.actor.typed.ActorSystem
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.Subscriptions
import org.apache.kafka.common.TopicPartition
import com.typesafe.config.ConfigFactory

object Consumer {

  // TODO remove
  trait Command

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    ???
  }

  def main(args: Array[String]): Unit = {

    implicit val system       = ActorSystem(Consumer(), "Consumer")
    implicit val slickSession = SlickSession.forConfig("slick-postgres")

    val config = ConfigFactory.load()

    val topic        = config.getString("topic")
    val subscription = Subscriptions.assignment(new TopicPartition(topic, 0))
  }

}
