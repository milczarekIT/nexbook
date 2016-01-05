package org.nexbook.concepts.pubsub

import org.nexbook.app.{DelegatorsProvider, Module}
import org.nexbook.core.Handler
import org.nexbook.neworderhandler.NewOrderHandler
import org.nexbook.neworderhandler.pubsub.PubSubNewOrderHandler
import org.nexbook.orderbookresponsehandler.pubsub.PubSubOrderBookResponseHandler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.orderchange.pubsub.PubSubOrderChangeHandler

/**
  * Created by milczu on 12/21/15.
  */
class PubSubModule extends Module with DelegatorsProvider {

  override def module: Module = this

  override def newOrderHandlers: List[NewOrderHandler] = List(new PubSubNewOrderHandler(orderHandlers))

  override def orderBookResponseHandlers: List[Handler[OrderBookResponse]] = List(new PubSubOrderBookResponseHandler(orderResponseHandlers))

  override def orderChangeHandlers: List[Handler[OrderChangeCommand]] = List(new PubSubOrderChangeHandler(orderChangeCommandHandlers))
}
