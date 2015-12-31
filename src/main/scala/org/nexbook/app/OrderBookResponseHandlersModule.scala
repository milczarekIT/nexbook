package org.nexbook.app

import org.nexbook.core.Handler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

/**
  * Created by milczu on 12/23/15.
  */
trait OrderBookResponseHandlersModule {

  def orderBookResponseHandlers: List[Handler[OrderBookResponse]]
}
