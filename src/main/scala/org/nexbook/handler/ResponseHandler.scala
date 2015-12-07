package org.nexbook.handler

import org.nexbook.orderprocessing.response.OrderProcessingResponse

/**
 * Created by milczu on 06.12.15.
 */
trait ResponseHandler {

  def handle(response: OrderProcessingResponse)
}
