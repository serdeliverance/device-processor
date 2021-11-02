package deviceprocessor

import java.util.UUID
import java.time.Instant

case class DeviceReading(devideId: UUID, currentValue: Double, unit: String, timestamp: Instant, version: Int)
