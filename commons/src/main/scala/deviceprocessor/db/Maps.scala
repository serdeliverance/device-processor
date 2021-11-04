package deviceprocessor.db

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp
import java.time.Instant

object Maps {
  val uuidToString = MappedColumnType.base[UUID, String](_.toString, UUID.fromString)

  val instantToTimestamp =
    MappedColumnType.base[Instant, Timestamp](instant => Timestamp.from(instant), timestamp => timestamp.toInstant())
}
