package deviceprocessor.domain

import java.util.UUID

case class AverageReading(deviceId: UUID, value: Double, unit: String)
