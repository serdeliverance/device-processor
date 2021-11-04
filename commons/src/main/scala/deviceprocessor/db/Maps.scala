package deviceprocessor.db

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp
import java.time.Instant

object Maps {
  val uuidToString = MappedColumnType.base[UUID, String](uuid => uuid.toString(), string => UUID.fromString(string))

  val instantToTimestamp =
    MappedColumnType.base[Instant, Timestamp](instant => Timestamp.from(instant), timestamp => timestamp.toInstant())
}
