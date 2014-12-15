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

    val orderHandler = new OrderHandler(orderBookRepository, orderRepository)

    /*val orderBuy1 = new LimitOrder(1, "EUR/USD", "Client1", Buy, 100.00, 1.25, DateTime.now(DateTimeZone.UTC))
    val orderBuy2 = new LimitOrder(2, "EUR/USD", "Client2", Buy, 200.00, 1.26, DateTime.now(DateTimeZone.UTC))
    val orderBuy3 = new LimitOrder(3, "EUR/USD", "Client3", Buy, 50.00, 1.24, DateTime.now(DateTimeZone.UTC))
    val orderSell1 = new MarketOrder(4, "PLN/USD", "Client4", Sell, 225.00, DateTime.now(DateTimeZone.UTC))

    orderHandler handle orderBuy1
    orderHandler handle orderBuy2
    orderHandler handle orderBuy3
    orderHandler handle orderSell1*/

    initFix
    LOGGER.info("FIX Acceptor Initialized")
  }

  def initFix() {
    val fixOrderHandlerSettings = new SessionSettings("config/fix_connection.config")
    val application = new FixOrderHandler
    val fileStoreFactory = new FileStoreFactory(fixOrderHandlerSettings)
    val messageFactory = new DefaultMessageFactory
    val fileLogFactory = new FileLogFactory(fixOrderHandlerSettings)
    val socketAcceptor = new SocketAcceptor(application, fileStoreFactory, fixOrderHandlerSettings, fileLogFactory, messageFactory)
    socketAcceptor.start
  }
}
