package org.nexbook.orderprocessing.publishsubscribe

import org.nexbook.orderprocessing.ProcessingResponseSender
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 25.08.15.
 */
class ProcessingResponsePublisher extends ProcessingResponseSender with mutable.Publisher[OrderProcessingResponse] {

  override def send(response: OrderProcessingResponse) = publish(response)

}
