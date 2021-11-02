package deviceprocessor.kafka

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

trait Unique {

  def id: String
}

object Unique {

  def uniqueId: String = s"${UUID.randomUUID}|${LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)}"

  def cleanUniqueId: String = s"${UUID.randomUUID}${LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)}".replace("-", "")
}
