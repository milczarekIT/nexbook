package org.nexbook.orderbookresponsehandler.handler

import org.nexbook.domain.{Execution, Order}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository}
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 09.12.15
  */
class TradeDatabaseSaver(orderDatabaseRepository: OrderDatabaseRepository, executionDatabaseRepository: ExecutionDatabaseRepository) extends OrderBookResponseHandler {
  val logger = LoggerFactory.getLogger(classOf[TradeDatabaseSaver])

  override def handle(response: OrderBookResponse): Unit = {
	response.payload match {
	  case o: Order =>
		orderDatabaseRepository add o
		logger.debug(s"Saved order: $o at ${System.currentTimeMillis}")
	  case e: Execution =>
		executionDatabaseRepository add e
		logger.debug(s"Saved execution: $e at ${System.currentTimeMillis}")
	}
  }
}
