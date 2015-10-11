package org.nexbook.actors

import akka.actor._
import org.nexbook.orderprocessing.OrderProcessingResponseHandler
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderProcessingResponse, OrderRejectionResponse}
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 11.10.15.
 */
class OrderProcessingResponseListener extends OrderProcessingResponseHandler with Actor {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponseListener])

  override def receive = {
    case OrderExecutionResponse(dealDone) => logger.info("Handled deal done: {}", dealDone)
    case OrderRejectionResponse(order, rejectReason) => logger.info("Rejection reject reason: {}", rejectReason)
  }

  override def handle(response: OrderProcessingResponse) = receive(response)
}
