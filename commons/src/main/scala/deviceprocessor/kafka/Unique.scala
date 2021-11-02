package deviceprocessor.kafka

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

/**
  * Trait that contains the concept of the a collision-free identifier.
  */
trait Unique {

  /**
    * Unique identifier that differentiates the current instance of others.
    *
    * @return
    */
  def id: String
}

/**
  * Contains the method that is used to generate unique ids.
  */
object Unique {

  def uniqueId: String = s"${UUID.randomUUID}|${LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)}"

  def cleanUniqueId: String = s"${UUID.randomUUID}${LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)}".replace("-", "")
}
