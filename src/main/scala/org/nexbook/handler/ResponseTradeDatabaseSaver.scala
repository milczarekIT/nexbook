package org.nexbook.handler

import org.nexbook.domain.{Execution, Order}
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository}
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 09.12.15
 */
class ResponseTradeDatabaseSaver(orderDatabaseRepository: OrderDatabaseRepository, executionDatabaseRepository: ExecutionDatabaseRepository) extends ResponseHandler {
  val logger = LoggerFactory.getLogger(classOf[ResponseTradeDatabaseSaver])

  override def handle(response: OrderProcessingResponse): Unit = response.payload match {
    case o: Order => orderDatabaseRepository add o
    case e: Execution => executionDatabaseRepository add e
  }
}
