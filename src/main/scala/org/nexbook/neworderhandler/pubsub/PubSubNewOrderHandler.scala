package org.nexbook.neworderhandler.pubsub

import org.nexbook.concepts.pubsub.{PubSubHandler, PubSubHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.domain.NewOrder

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
class PubSubNewOrderHandler(delegators: List[Handler[NewOrder]]) extends PubSubHandler[NewOrder] {
  override val subscribers: List[mutable.Subscriber[NewOrder, mutable.Publisher[NewOrder]]] = delegators.map(new PubSubHandlerWrapper[NewOrder](_))

  init
}
