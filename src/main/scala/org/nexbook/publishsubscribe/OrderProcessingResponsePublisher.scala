package org.nexbook.publishsubscribe

import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.orderprocessing.{OrderProcessingResponseHandler, OrderProcessingResponseSender}

import scala.collection.mutable

/**
 * Created by milczu on 25.08.15.
 */
class OrderProcessingResponsePublisher extends OrderProcessingResponseSender with mutable.Publisher[OrderProcessingResponse] {

  override def send(response: OrderProcessingResponse) = publish(response)
}
