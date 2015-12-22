package org.nexbook.concepts.pubsub

import org.nexbook.core.Handler

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
class PubSubHandlerWrapper[T](handler: Handler[T]) extends mutable.Subscriber[T, mutable.Publisher[T]] {
  override def notify(pub: mutable.Publisher[T], event: T): Unit = handler handle event
}
