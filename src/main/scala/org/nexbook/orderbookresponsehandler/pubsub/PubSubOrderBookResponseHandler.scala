package org.nexbook.orderbookresponsehandler.pubsub

import org.nexbook.concepts.pubsub.{PubSubHandler, PubSubHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

import scala.collection.mutable

/**
  * Created by milczu on 25.08.15.
  */
class PubSubOrderBookResponseHandler(delegators: List[Handler[OrderBookResponse]]) extends PubSubHandler[OrderBookResponse] {
  override def subscribers: List[mutable.Subscriber[OrderBookResponse, mutable.Publisher[OrderBookResponse]]] = delegators.map(new PubSubHandlerWrapper[OrderBookResponse](_))

  init
}
