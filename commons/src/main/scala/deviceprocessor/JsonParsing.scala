package deviceprocessor

import io.circe._
import io.circe.generic.extras.semiauto._
import com.challenge.deviceprocessor.Device
import io.circe.generic.extras.Configuration

object JsonParsing {

  implicit val customPrinter: Printer      = Printer.noSpaces.copy(dropNullValues = true)
  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames

  implicit val deviceDecoder: Decoder[Device] = deriveConfiguredDecoder
  implicit val deviceEncoder: Encoder[Device] = deriveConfiguredEncoder

  implicit val deviceReadingDecoder: Decoder[DeviceReading] = deriveConfiguredDecoder
  implicit val deviceReadingEncoder: Encoder[DeviceReading] = deriveConfiguredEncoder
}
