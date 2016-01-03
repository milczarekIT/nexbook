package org.nexbook.app

import java.util.concurrent.atomic.AtomicBoolean

import com.softwaremill.macwire._
import org.nexbook.concepts.akka.AkkaModule
import org.nexbook.concepts.pubsub.PubSubModule
import org.nexbook.fix.{FixEngineRunner, FixMessageHandler}
import org.slf4j.LoggerFactory

object OrderBookApp extends BasicComponentProvider {

  val logger = LoggerFactory.getLogger(classOf[App])
  val mode = AppConfig.mode
  val runningMode = AppConfig.runningMode
  val appWorking = new AtomicBoolean(true)

  val module: Module = mode match {
	case PubSub => wire[PubSubModule]
	case Akka => wire[AkkaModule]
  }
  val orderResponseHandlers = module.orderBookResponseHandlers

  val fixMessageHandler: FixMessageHandler = wire[FixMessageHandler]

  def main(args: Array[String]) {
	logger.info(s"NexBook starting, config name: ${AppConfig.configName}, app mode: $mode, running mode: $runningMode")

	if(Live == runningMode) {
	  val fixEngineRunner = new FixEngineRunner(fixMessageHandler, AppConfig.fixConfigPath)
	  fixEngineRunner.run()
	} else {
	  while (appWorking.get) {
		logger.info("App is working")
		Thread.sleep(15000)
	  }
	}
  }

  def stop(): Unit = {
	logger.info("Stop App")
	appWorking.compareAndSet(true, false)
  }


}
