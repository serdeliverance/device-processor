package deviceprocessor.utils

import deviceprocessor.domain.AverageReading
import deviceprocessor.domain.LastReading

object FormatUtils {

  private val SEPARATOR = "\n"

  private def createHeader(headerStr: String): String = s"""
        | ------------------
        |$headerStr
        | ------------------
    """.stripMargin

  private def templateForContent(header: String, separator: String = SEPARATOR)(content: String): String =
    createHeader(header) ++ separator ++ content

  def formatAverageReadings(list: List[AverageReading]): String =
    templateForContent("Average readings:") {
      list
        .map(reading => s"device=${reading.deviceId}, average=${reading.value}")
        .mkString(SEPARATOR)
    }

  def formatLastReadings(list: List[LastReading]) =
    templateForContent("Last readings:") {
      list
        .map(reading => s"device=${reading.deviceId}, last-value=${reading.value}")
        .mkString(SEPARATOR, SEPARATOR, SEPARATOR)
    }
}
