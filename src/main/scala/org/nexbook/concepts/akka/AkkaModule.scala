package org.nexbook.concepts.akka

import org.nexbook.app.{DelegatorsProvider, Module}
import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.akka.{AkkaNewOrderCancelHandler, AkkaNewOrderHandler}
import org.nexbook.orderbookresponsehandler.akka.AkkaOrderBookResponseHandler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

/**
  * Created by milczu on 12/21/15.
  */
class AkkaModule extends Module with DelegatorsProvider {

  override def module: Module = this

  override lazy val newOrderHandlers: List[Handler[NewOrder]] = List(new AkkaNewOrderHandler(orderHandlers))

  override lazy val newOrderCancelsHandlers: List[Handler[NewOrderCancel]] = List(new AkkaNewOrderCancelHandler(orderCancelHandlers))

  override lazy val orderBookResponseHandlers: List[Handler[OrderBookResponse]] = List(new AkkaOrderBookResponseHandler(orderResponseHandler))
}
