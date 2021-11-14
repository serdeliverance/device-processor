package deviceprocessor.queue

import deviceprocessor.queue.TimeBoundedDQueue.TimedEntry

import java.time.Instant
import java.time.temporal.ChronoUnit

case class TimeBoundedDQueue[T] private (elements: List[TimedEntry[T]], timeWindowInMinutes: Int) extends DQueue[T] {

  override def enqueue(t: T): DQueue[T] = {
    val lowBoundTimeWindow = Instant.now().minus(timeWindowInMinutes, ChronoUnit.MINUTES)
    val elementsInsideTimeWindow: List[TimedEntry[T]] =
      elements.filter(elem => elem.enqueuedAt.isAfter(lowBoundTimeWindow))
    this.copy(elements = elementsInsideTimeWindow :+ TimedEntry(t))
  }

  override def dequeue: (T, DQueue[T]) = elements match {
    case ::(head, tail) => (head.entity, new TimeBoundedDQueue[T](tail, timeWindowInMinutes))
    case Nil            => throw new RuntimeException("dequeue on empty queue")
  }

  override def getAll: List[T] = elements.map(_.entity)
}

object TimeBoundedDQueue {

  case class TimedEntry[T](entity: T, enqueuedAt: Instant = Instant.now)

  def apply[T](timeWindow: Int = 2): TimeBoundedDQueue[T] = new TimeBoundedDQueue[T](List.empty, timeWindow)
}
