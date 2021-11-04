package deviceprocessor.domain

import java.util.UUID
import java.time.Instant

case class DeviceReading(
  deviceId: UUID,
  currentValue: Double,
  unit: String,
  timestamp: Instant,
  version: Int,
  id: Option[Long] = None
)
