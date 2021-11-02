package deviceprocessor.kafka

import com.typesafe.config.{Config, ConfigFactory}

class MessageProducerConfiguration(val bootstrapServers: String)

object MessageProducerConfiguration extends BrokerClient {

  def apply(configuration: Config = ConfigFactory.load): MessageProducerConfiguration =
    new MessageProducerConfiguration(
      bootstrapServers = safeGet(_.getString(getSetting("bootstrap-servers")), "localhost:9092")(configuration)
    )

  override protected val CLIENT_SECTION: Option[String] = Some("producer")
}
