package org.nexbook.orderchange.pubsub

import org.nexbook.concepts.pubsub.{PubSubHandler, PubSubHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.orderchange.OrderChangeCommand

import scala.collection.mutable

/**
  * Created by milczu on 12/23/15.
  */
class PubSubOrderChangeHandler(delegators: List[Handler[OrderChangeCommand]]) extends PubSubHandler[OrderChangeCommand] {
  override def subscribers: List[mutable.Subscriber[OrderChangeCommand, mutable.Publisher[OrderChangeCommand]]] = delegators.map(new PubSubHandlerWrapper[OrderChangeCommand](_))

  init
}
