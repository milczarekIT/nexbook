package org.nexbook.core

import com.typesafe.config.ConfigFactory
import org.nexbook.actors.ActorsOrderProcessingResponseLifecycleFactory
import org.nexbook.fix.{FixOrderHandlerRunner, FixOrderHandler}
import org.nexbook.orderprocessing.OrderProcessingResponseLifecycleFactory
import org.nexbook.publishsubscribe.{PubSubOrderProcessingResponseLifecycleFactory}
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory
import com.softwaremill.macwire._

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val FIX_CONFIGURATION_FILE = "config/fix_connection.config"
  val config = ConfigFactory.load()

  val orderBookRepository = wire[OrderBookRepository]
  val orderRepository = wire[OrderRepository]
  val orderHandler = wire[OrderHandler]
  val mode = config.getString("org.nexbook.mode")
  val lifecycleFactory = resolveLifeCycleFactory

  def resolveLifeCycleFactory: OrderProcessingResponseLifecycleFactory = {
    if(mode.equals("PubSub")) {
      wire[PubSubOrderProcessingResponseLifecycleFactory]
    } else if(mode.equals("Actors")) {
      wire[ActorsOrderProcessingResponseLifecycleFactory]
    } else {
      throw new IllegalArgumentException
    }
  }

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    LOGGER.debug("Mode: {}", mode)



    val fixOrderHandler = wire[FixOrderHandler]
    val fixOrderHandlerRunner = new FixOrderHandlerRunner(fixOrderHandler, FIX_CONFIGURATION_FILE)
    fixOrderHandlerRunner.run
  }
}
