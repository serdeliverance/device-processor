package deviceprocessor.actor

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import deviceprocessor.actor.LastReadingTrackerActor.{GetLastReads, Process}
import deviceprocessor.actor.MetricsAsker.ReceiveLastReadings
import deviceprocessor.domain.LastReading
import deviceprocessor.stubs.DeviceStubs
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class LastReadingTrackerActorSpec extends AnyWordSpec with BeforeAndAfterAll with Matchers with DeviceStubs {

  val testkit = ActorTestKit()

  "retrieving last read" in {
    val probe              = testkit.createTestProbe[ReceiveLastReadings]()
    val lastReadingTracker = testkit.spawn(LastReadingTrackerActor(), "lastReadingTracker")

    val secondReading = deviceReading.copy(currentValue = 200)

    lastReadingTracker ! Process(deviceReading)
    lastReadingTracker ! Process(secondReading)
    lastReadingTracker ! GetLastReads(probe.ref)

    probe.expectMessage(ReceiveLastReadings(List(LastReading(secondReading.deviceId, secondReading.currentValue))))

    testkit.stop(lastReadingTracker, 3.seconds)
  }

  override def afterAll() = testkit.shutdownTestKit()
}
