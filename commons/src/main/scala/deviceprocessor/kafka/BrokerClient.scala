package deviceprocessor.kafka

trait BrokerClient {

  protected val BROKER_SECTION: String = "message-broker"

  protected val CLIENT_SECTION: Option[String] = None

  protected def getSetting(setting: String): String =
    s"$BROKER_SECTION.${CLIENT_SECTION.map(clientSection => s"$clientSection.").getOrElse("")}$setting"
}
