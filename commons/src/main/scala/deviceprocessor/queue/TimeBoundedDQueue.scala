package deviceprocessor.queue

import deviceprocessor.queue.TimeBoundedDQueue.TimedEntry

import java.time.Instant
import java.time.temporal.ChronoUnit

class TimeBoundedDQueue[T] private (elements: List[TimedEntry[T]], timeWindow) extends DQueue[T] {

  override def enqueue(t: T): DQueue[T] = {
    val lowBoundTimeWindow = Instant.now().minus(timeWindow, ChronoUnit.MINUTES)
    val elementsInsideTimeWindow: List[TimedEntry[T]] =
      elementsInsideTimeWindow.filter(elem => elem.enqueuedAt.isAfter(lowBoundTimeWindow))
    new TimeBoundedDQueue[T](elementsInsideTimeWindow :+ TimedEntry(t), timeWindow)
  }

  override def dequeue: (T, DQueue[T]) = elements match {
    case ::(head, tail) => (head.entity, new TimeBoundedDQueue[T](tail, timeWindow))
    case Nil            => throw new RuntimeException("dequeue on empty queue")
  }

  override def getAll: List[T] = elements.map(_.entity)
}

object TimeBoundedDQueue {

  case class TimedEntry[T](entity: T, enqueuedAt: Instant = Instant.now)

  def apply[T](timeWindow: Int = 2): TimeBoundedDQueue[T] = new TimeBoundedDQueue[T](List.empty)
}
