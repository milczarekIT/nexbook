package org.nexbook.core

import org.nexbook.fix.{FixOrderHandlerRunner, FixOrderHandler}
import org.nexbook.orderprocessing.OrderProcessingResponseLifecycleFactory
import org.nexbook.publishsubscribe.{PubSubOrderProcessingResponseLifecycleFactory, OrderProcessingResponseSubscriber}
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory
import quickfix._
import com.softwaremill.macwire._

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val FIX_CONFIGURATION_FILE = "config/fix_connection.config"

  val orderBookRepository = wire[OrderBookRepository]
  val orderRepository = wire[OrderRepository]
  val lifecycleFactory = wire[PubSubOrderProcessingResponseLifecycleFactory]
  val orderHandler = wire[OrderHandler]

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    val fixOrderHandler = wire[FixOrderHandler]
    val fixOrderHandlerRunner = new FixOrderHandlerRunner(fixOrderHandler, FIX_CONFIGURATION_FILE)
    fixOrderHandlerRunner.run
  }
}
