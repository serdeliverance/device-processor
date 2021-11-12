package deviceprocessor.queue

import deviceprocessor.queue.TimeBoundedQueue.TimedEntry

import java.time.Instant
import java.time.temporal.ChronoUnit

class TimeBoundedQueue[T] private (elements: List[TimedEntry[T]], timeWindow: Int = 2) extends DQueue[T] {

  override def enqueue(t: T): DQueue[T] = {
    val lowBoundTimeWindow = Instant.now().minus(timeWindow, ChronoUnit.MINUTES)
    val elementsInsideTimeWindow: List[TimedEntry[T]] =
      elementsInsideTimeWindow.filter(elem => elem.enqueuedAt.isAfter(lowBoundTimeWindow))
    new TimeBoundedQueue[T](elementsInsideTimeWindow :+ TimedEntry(t))
  }

  override def dequeue: (T, DQueue[T]) = elements match {
    case ::(head, tail) => (head.entity, new TimeBoundedQueue[T](tail))
    case Nil            => throw new RuntimeException("dequeue on empty queue")
  }

  override def getAll: List[T] = elements.map(_.entity)
}

object TimeBoundedQueue {

  case class TimedEntry[T](entity: T, enqueuedAt: Instant = Instant.now)

  def apply[T](): TimeBoundedQueue[T] = new TimeBoundedQueue[T](List.empty)
}
