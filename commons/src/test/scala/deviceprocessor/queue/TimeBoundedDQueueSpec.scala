package deviceprocessor.queue

import deviceprocessor.domain.DeviceReading
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import java.util.UUID
import scala.util.{Failure, Success, Try}

class TimeBoundedDQueueSpec extends AnyWordSpec with Matchers {

  private val readingOne = DeviceReading(UUID.randomUUID(), 10.0, "farenheit", Instant.now(), 1)
  private val readingTwo = DeviceReading(UUID.randomUUID(), 20.0, "farenheit", Instant.now(), 1)

  "enqueue reading correctly" in {
    val queue  = TimeBoundedDQueue[DeviceReading]()
    val result = queue.enqueue(readingOne).enqueue(readingTwo)

    result.getAll mustBe List(readingOne, readingTwo)
  }

  "enqueue must retain elements inside time window" in {
    // TODO
  }

  "dequeue correctly" in {
    val readingThree = DeviceReading(UUID.randomUUID(), 30.0, "farenheit", Instant.now(), 1)
    val queue        = TimeBoundedDQueue[DeviceReading]().enqueue(readingOne).enqueue(readingTwo).enqueue(readingThree)

    val (dequeuedElement, updatedQueue) = queue.dequeue

    dequeuedElement mustBe readingOne
    updatedQueue.getAll mustBe List(readingTwo, readingThree)

  }

  "fail dequeing when queue is empty" in {
    val emptyQueue = TimeBoundedDQueue[DeviceReading]()

    val result = Try(emptyQueue.dequeue)

    result match {
      case Failure(_) => succeed
      case Success(_) => fail()
    }
  }

  "get all elements from empty queue must return empty result" in {
    val emptyQueue = TimeBoundedDQueue[DeviceReading]()

    val result = emptyQueue.getAll

    result mustBe List.empty
  }
}
