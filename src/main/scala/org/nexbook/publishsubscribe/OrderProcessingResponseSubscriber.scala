package org.nexbook.publishsubscribe

import org.nexbook.orderprocessing.OrderProcessingResponseHandler
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderRejectionResponse, OrderProcessingResponse}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 18.08.15.
 */
class OrderProcessingResponseSubscriber(pub: mutable.Publisher[OrderProcessingResponse]) extends OrderProcessingResponseHandler with mutable.Subscriber[OrderProcessingResponse, mutable.Publisher[OrderProcessingResponse]] {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponseSubscriber])

  override def handle(response: OrderProcessingResponse) {
    notify(pub, response)
  }

  override def notify(pub: mutable.Publisher[OrderProcessingResponse], event: OrderProcessingResponse) = event match {
    case execution: OrderExecutionResponse => logger.info("Handled deal done: {}", execution.dealDone)
    case rejection: OrderRejectionResponse => logger.info("Rejection reject reason: {}", rejection.rejectReason)
  }
}
