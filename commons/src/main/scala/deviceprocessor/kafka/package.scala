package deviceprocessor

import akka.kafka.ConsumerMessage.CommittableMessage
import com.typesafe.config.Config

import scala.util.{Failure, Success, Try}

package object kafka {

  type StreamMessage = CommittableMessage[String, Array[Byte]]

  def safeGet[T](getter: Config => T, default: T)(implicit configuration: Config): T =
    Try(getter(configuration)) match {
      case Failure(_)     => default
      case Success(value) => value
    }
}
