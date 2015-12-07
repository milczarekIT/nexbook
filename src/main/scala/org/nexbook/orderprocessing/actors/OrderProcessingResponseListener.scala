package org.nexbook.orderprocessing.actors

import akka.actor._
import org.nexbook.orderprocessing.OrderProcessingResponseHandler
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory


/**
 * Created by milczu on 11.10.15.
 */
class OrderProcessingResponseListener extends OrderProcessingResponseHandler with Actor {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponseListener])

  override def receive = {
    case response: OrderProcessingResponse => logger.info("received: {}", response) //TODO// generalResponseHandler doHandle response
  }

  override def handle(response: OrderProcessingResponse) = receive(response)
}
