package org.nexbook.handler

import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderProcessingResponse, OrderRejectionResponse}
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 06.12.15.
 */
class ResponseLoggingHandler extends ResponseHandler {

  val logger = LoggerFactory.getLogger(classOf[ResponseLoggingHandler])

  override def handle(response: OrderProcessingResponse) = response match {
    case execution: OrderExecutionResponse => logger.info("Handled deal done: {}", execution.dealDone)
    case rejection: OrderRejectionResponse => logger.info("Rejection reject reason: {}", rejection.rejection)
    case other => logger.info("Other: {}", other)
  }
}
