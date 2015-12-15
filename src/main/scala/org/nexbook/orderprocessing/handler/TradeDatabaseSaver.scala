package org.nexbook.orderprocessing.handler

import org.nexbook.domain.{Execution, Order}
import org.nexbook.orderprocessing.ProcessingResponseHandler
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository}
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 09.12.15
 */
class TradeDatabaseSaver(orderDatabaseRepository: OrderDatabaseRepository, executionDatabaseRepository: ExecutionDatabaseRepository) extends ProcessingResponseHandler {
  val logger = LoggerFactory.getLogger(classOf[TradeDatabaseSaver])

  override def handle(response: OrderProcessingResponse): Unit = this.synchronized {
    response.payload match {
      case o: Order => orderDatabaseRepository add o
      case e: Execution => executionDatabaseRepository add e
    }
  }
}
