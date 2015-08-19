package org.nexbook.core

import org.nexbook.fix.FixOrderHandler
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory
import quickfix._
import com.softwaremill.macwire._

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val FIX_CONFIGURATION_FILE = "config/fix_connection.config"

  val orderBookRepository = wire[OrderBookRepository]
  val orderRepository = wire[OrderRepository]
  val executionHandler = wire[ExecutionHandler]
  val orderHandler = wire[OrderHandler]

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    val fixOrderHandler = wire[FixOrderHandler]
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
