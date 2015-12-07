package org.nexbook.orderprocessing.publishsubscribe

import org.nexbook.handler.GeneralResponseHandler
import org.nexbook.orderprocessing.OrderProcessingResponseHandler
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 18.08.15.
 */
class OrderProcessingResponseSubscriber(pub: mutable.Publisher[OrderProcessingResponse], generalResponseHandler: GeneralResponseHandler) extends OrderProcessingResponseHandler with mutable.Subscriber[OrderProcessingResponse, mutable.Publisher[OrderProcessingResponse]] {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponseSubscriber])

  override def handle(response: OrderProcessingResponse) {
    notify(pub, response)
  }

  override def notify(pub: mutable.Publisher[OrderProcessingResponse], event: OrderProcessingResponse) = {
    logger.debug("do handle event: {} - " + generalResponseHandler, event)
    generalResponseHandler doHandle event
  }
}
