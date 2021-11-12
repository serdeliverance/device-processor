package deviceprocessor.queue

import deviceprocessor.queue.TimeBoundedQueue.TimedEntry

import java.time.LocalDateTime
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class TimeBoundedQueue[T] private (elements: List[TimedEntry[T]], timeWindow: FiniteDuration = 2.minutes)
    extends DQueue[T] {

  override def enqueue(t: T): DQueue[T] = {
    // TODO filter elements that match inside time window
    val elementsInTimeWindow: List[TimedEntry[T]] = ???
    new TimeBoundedQueue[T](elements :+ TimedEntry(t))
  }

  override def dequeue: (T, DQueue[T]) = elements match {
    case ::(head, tail) => (head.entity, new TimeBoundedQueue[T](tail))
    case Nil            => throw new RuntimeException("dequeue on empty queue")
  }

  override def getAll: List[T] = elements.map(_.entity)
}

object TimeBoundedQueue {

  case class TimedEntry[T](entity: T, enqueuedAt: LocalDateTime = LocalDateTime.now)

  def apply[T](): TimeBoundedQueue[T] = new TimeBoundedQueue[T](List.empty)
}
