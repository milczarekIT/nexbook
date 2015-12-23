package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.concepts.akka.AkkaModule
import org.nexbook.concepts.pubsub.PubSubModule
import org.nexbook.fix.{FixEngineRunner, FixMessageHandler}
import org.slf4j.LoggerFactory

object OrderBookApp extends BasicComponentProvider {

  val LOGGER = LoggerFactory.getLogger(classOf[App])
  val mode = AppConfig.mode

  val module: Module = mode match {
	case PubSub => new PubSubModule
	case Akka => wire[AkkaModule]
  }
  val orderResponseHandlers = module.orderBookResponseHandlers

  def main(args: Array[String]) {
	LOGGER.info("NexBook starting")
	LOGGER.debug("Mode: {}", mode)

	val fixMessageHandler: FixMessageHandler = wire[FixMessageHandler]
	val fixEngineRunner = new FixEngineRunner(fixMessageHandler, AppConfig.fixConfigPath)
	fixEngineRunner.run
  }


}
