package org.nexbook.orderprocessing

import org.nexbook.orderprocessing.response.OrderProcessingResponse

/**
 * Created by milczu on 25.08.15.
 */
trait OrderProcessingResponseSender {

  def send(response: OrderProcessingResponse)

  def registerHandler(handler: OrderProcessingResponseHandler)
}
