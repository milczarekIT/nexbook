package org.nexbook.app

import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory

/**
  * Created by milczu on 07.01.16.
  */
class WaitingLoopRunner extends ApplicationRunner {

  val logger = LoggerFactory.getLogger(classOf[WaitingLoopRunner])
  val appWorking = new AtomicBoolean(true)

  override def start(): Unit = {
	while (appWorking.get) {
	  logger.info("App is working")
	  Thread.sleep(15000)
	}
  }

  override def stop(): Unit = appWorking.compareAndSet(true, false)
}
