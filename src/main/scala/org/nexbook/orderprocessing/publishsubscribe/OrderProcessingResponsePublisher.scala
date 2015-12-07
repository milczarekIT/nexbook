package org.nexbook.orderprocessing.publishsubscribe

import org.nexbook.orderprocessing.OrderProcessingResponseSender
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 25.08.15.
 */
class OrderProcessingResponsePublisher extends OrderProcessingResponseSender with mutable.Publisher[OrderProcessingResponse] {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponsePublisher])

  override def send(response: OrderProcessingResponse) = publish(response)

}
