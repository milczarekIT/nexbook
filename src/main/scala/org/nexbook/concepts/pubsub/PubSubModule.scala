package org.nexbook.concepts.pubsub

import org.nexbook.app.{DelegatorsProvider, Module}
import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.pubsub.{PubSubNewOrderCancelHandler, PubSubNewOrderHandler}
import org.nexbook.orderbookresponsehandler.pubsub.PubSubOrderBookResponseHandler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.orderchange.pubsub.PubSubOrderChangeHandler

/**
  * Created by milczu on 12/21/15.
  */
class PubSubModule extends Module with DelegatorsProvider {

  override def module: Module = this

  override def newOrderHandlers: List[Handler[NewOrder]] = List(new PubSubNewOrderHandler(orderHandlers))

  override def newOrderCancelsHandlers: List[Handler[NewOrderCancel]] = List(new PubSubNewOrderCancelHandler(orderCancelHandlers))

  override def orderBookResponseHandlers: List[Handler[OrderBookResponse]] = List(new PubSubOrderBookResponseHandler(orderResponseHandlers))

  override def orderChangeHandlers: List[Handler[OrderChangeCommand]] = List(new PubSubOrderChangeHandler(orderChangeCommandHandlers))
}
