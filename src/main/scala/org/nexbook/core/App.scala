package org.nexbook.core

import org.nexbook.fix.FixOrderHandler
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory
import quickfix._

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    val currencyPairs = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")
    val orderBookRepository = new OrderBookRepository(currencyPairs)
    val orderRepository = new OrderRepository

    val orderHandler = new OrderHandler(new Sequencer, orderBookRepository, orderRepository)
    val fixOrderHandler = new FixOrderHandler(orderHandler)




    initFix(fixOrderHandler)
    LOGGER.info("FIX Acceptor Initialized")
  }

  def initFix(fixApplication: Application) {
    val fixOrderHandlerSettings = new SessionSettings("config/fix_connection.config")
    val fileStoreFactory = new FileStoreFactory(fixOrderHandlerSettings)
    val messageFactory = new DefaultMessageFactory
    val fileLogFactory = new FileLogFactory(fixOrderHandlerSettings)
    val socketAcceptor = new SocketAcceptor(fixApplication, fileStoreFactory, fixOrderHandlerSettings, fileLogFactory, messageFactory)
    socketAcceptor.start
  }
}
