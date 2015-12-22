package org.nexbook.neworderhandler.pubsub

import org.nexbook.concepts.pubsub.{PubSubHandler, PubSubHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.domain.NewOrderCancel

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
class PubSubNewOrderCancelHandler(delegators: List[Handler[NewOrderCancel]]) extends PubSubHandler[NewOrderCancel] {
  override def subscribers: List[mutable.Subscriber[NewOrderCancel, mutable.Publisher[NewOrderCancel]]] = delegators.map(new PubSubHandlerWrapper[NewOrderCancel](_))

  init
}
