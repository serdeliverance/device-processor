package deviceprocessor.stubs

import deviceprocessor.domain.DeviceReading

import java.time.Instant
import java.util.UUID

trait DeviceStubs {
  lazy val deviceReading = DeviceReading(UUID.randomUUID(), 100, "farenheit", Instant.now(), 1)
}
