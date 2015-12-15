package org.nexbook.orderprocessing

import org.nexbook.orderprocessing.response.OrderProcessingResponse

/**
 * Created by milczu on 06.12.15.
 */
trait ProcessingResponseHandler {

  def handle(response: OrderProcessingResponse)
}
