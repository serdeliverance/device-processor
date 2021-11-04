package deviceprocessor.db

import slick.jdbc.PostgresProfile.api._
import deviceprocessor.db.Maps
import deviceprocessor.domain._
import java.util.UUID
import java.time.Instant

class DeviceReadingTable(tag: Tag) extends Table[DeviceReading](tag, "device_reading") {

  def id: Rep[Option[Long]]     = column[Option[Long]]("id", O.PrimaryKey)
  def deviceId: Rep[UUID]       = column[UUID]("device_id")(Maps.uuidToString)
  def currentValue: Rep[Double] = column[Double]("current_value")
  def unit: Rep[String]         = column[String]("unit")
  def timestamp: Rep[Instant]   = column[Instant]("timestamp")(Maps.instantToTimestamp)
  def version: Rep[Int]         = column[Int]("version")

  override def * = (deviceId, currentValue, unit, timestamp, version, id).mapTo[DeviceReading]
}

object DeviceReadingTable {
  lazy val deviceReadingTable = TableQuery[DeviceReadingTable]
}
