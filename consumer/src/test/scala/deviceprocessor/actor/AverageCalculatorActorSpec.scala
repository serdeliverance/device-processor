package deviceprocessor.actor

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import deviceprocessor.actor.AverageCalculatorActor.{GetAverage, Process}
import deviceprocessor.actor.MetricsAsker.ReceiveAverageReadings
import deviceprocessor.domain.AverageReading
import deviceprocessor.stubs.DeviceStubs
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class AverageCalculatorActorSpec extends AnyWordSpec with BeforeAndAfterAll with Matchers with DeviceStubs {

  val testkit = ActorTestKit()

  "calculate average value" in {
    val probe             = testkit.createTestProbe[ReceiveAverageReadings]()
    val averageCalculator = testkit.spawn(AverageCalculatorActor(), "AverageCalculator")

    val firstReadingValue  = 50
    val secondReadingValue = 30

    averageCalculator ! Process(deviceReading.copy(currentValue = firstReadingValue))
    averageCalculator ! Process(deviceReading.copy(currentValue = secondReadingValue))
    averageCalculator ! GetAverage(probe.ref)

    val expectedReading = AverageReading(deviceReading.deviceId, 40)

    probe.expectMessage(ReceiveAverageReadings(List(expectedReading)))

    testkit.stop(averageCalculator, 3.seconds)
  }

  override def afterAll(): Unit = testkit.shutdownTestKit()
}
