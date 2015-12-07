package org.nexbook.handler

import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 06.12.15.
 */
class ResponseFixResponseSender extends ResponseHandler {

  val logger = LoggerFactory.getLogger(classOf[ResponseFixResponseSender])

  override def handle(response: OrderProcessingResponse) = {
    // TODO
    logger.debug("do send fix: {}", response)
  }

//  def toFixMessage(response: OrderProcessingResponse): Message = {
//
//  }
}
