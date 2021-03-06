package deviceprocessor.json

import io.circe._
import io.circe.generic.extras.semiauto._
import io.circe.generic.extras.Configuration

import deviceprocessor.domain._

object JsonParsing {

  implicit val customPrinter: Printer      = Printer.noSpaces.copy(dropNullValues = true)
  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames

  implicit val deviceDecoder: Decoder[Device] = deriveConfiguredDecoder
  implicit val deviceEncoder: Encoder[Device] = deriveConfiguredEncoder

  implicit val deviceReadingDecoder: Decoder[DeviceReading] = deriveConfiguredDecoder
  implicit val deviceReadingEncoder: Encoder[DeviceReading] = deriveConfiguredEncoder
}
