package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.concepts.akka.AkkaModule
import org.nexbook.concepts.pubsub.PubSubModule
import org.nexbook.fix.{FixEngineRunner, FixMessageHandler}
import org.slf4j.LoggerFactory

object OrderBookApp extends BasicComponentProvider {

  val logger = LoggerFactory.getLogger(classOf[App])
  val mode = AppConfig.mode
  val runningMode = AppConfig.runningMode

  val module: Module = mode match {
	case PubSub => wire[PubSubModule]
	case Akka => wire[AkkaModule]
  }

  val fixMessageHandler: FixMessageHandler = wire[FixMessageHandler]
  val applicationRunner: ApplicationRunner = if (Live == runningMode) new FixEngineRunner(fixMessageHandler, AppConfig.fixConfigPath) else new WaitingLoopRunner

  def main(args: Array[String]) {
	logger.info(s"NexBook starting, config name: ${AppConfig.configName}, app mode: $mode, running mode: $runningMode")

	applicationRunner.start()
  }

  def stop(): Unit = {
	logger.info("Stop App")
	applicationRunner.stop()
  }


}
