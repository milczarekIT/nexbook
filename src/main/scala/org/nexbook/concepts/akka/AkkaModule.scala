package org.nexbook.concepts.akka

import org.nexbook.app.{DelegatorsProvider, Module}
import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.akka.{AkkaNewOrderCancelHandler, AkkaNewOrderHandler}
import org.nexbook.orderbookresponsehandler.akka.AkkaOrderBookResponseHandler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.orderchange.akka.AkkaOrderChangeHandler

/**
  * Created by milczu on 12/21/15.
  */
class AkkaModule extends Module with DelegatorsProvider {

  override def module: Module = this

  override def newOrderHandlers: List[Handler[NewOrder]] = List(new AkkaNewOrderHandler(orderHandlers))

  override def newOrderCancelsHandlers: List[Handler[NewOrderCancel]] = List(new AkkaNewOrderCancelHandler(orderCancelHandlers))

  override def orderBookResponseHandlers: List[Handler[OrderBookResponse]] = List(new AkkaOrderBookResponseHandler(orderResponseHandlers))

  override def orderChangeHandlers: List[Handler[OrderChangeCommand]] = List(new AkkaOrderChangeHandler(orderChangeCommandHandlers))
}
