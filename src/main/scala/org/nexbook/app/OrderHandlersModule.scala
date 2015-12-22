package org.nexbook.app

import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

/**
  * Created by milczu on 12/21/15.
  */
trait OrderHandlersModule {

  def newOrderHandlers: List[Handler[NewOrder]]

  def newOrderCancelsHandlers: List[Handler[NewOrderCancel]]

  def orderResponseHandlers: List[Handler[OrderBookResponse]]
}
