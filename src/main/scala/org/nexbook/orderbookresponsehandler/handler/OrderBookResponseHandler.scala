package org.nexbook.orderbookresponsehandler.handler

import org.nexbook.core.Handler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

/**
 * Created by milczu on 06.12.15.
 */
trait OrderBookResponseHandler extends Handler[OrderBookResponse] {

  def handle(response: OrderBookResponse)
}
