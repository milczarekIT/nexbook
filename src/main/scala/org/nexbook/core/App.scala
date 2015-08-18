package org.nexbook.core

import org.nexbook.fix.FixOrderHandler
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory
import quickfix._

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val FIX_CONFIGURATION_FILE = "config/fix_connection.config";
  val SUPPORTED_CURRENCY_PAIRS = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    val orderBookRepository = new OrderBookRepository(SUPPORTED_CURRENCY_PAIRS)
    val orderRepository = new OrderRepository

    val orderHandler = new OrderHandler(new Sequencer, orderBookRepository, orderRepository, new ExecutionHandler)
    val fixOrderHandler = new FixOrderHandler(orderHandler)


    initFix(fixOrderHandler)
    LOGGER.info("FIX Acceptor Initialized")
  }

  def initFix(fixApplication: Application) {
    val fixOrderHandlerSettings = new SessionSettings(FIX_CONFIGURATION_FILE)
    val fileStoreFactory = new FileStoreFactory(fixOrderHandlerSettings)
    val messageFactory = new DefaultMessageFactory
    val fileLogFactory = new FileLogFactory(fixOrderHandlerSettings)
    val socketAcceptor = new SocketAcceptor(fixApplication, fileStoreFactory, fixOrderHandlerSettings, fileLogFactory, messageFactory)
    socketAcceptor.start
  }
}
