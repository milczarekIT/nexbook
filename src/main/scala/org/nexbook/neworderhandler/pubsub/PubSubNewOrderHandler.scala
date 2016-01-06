package org.nexbook.neworderhandler.pubsub

import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.NewOrderHandler

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
class PubSubNewOrderHandler(delegators: List[NewOrderHandler]) extends NewOrderHandler {
  val newOrderPublisher = new GenericPublisher[NewOrder]
  val cancelOrderPublisher = new GenericPublisher[NewOrderCancel]

  val subscribersNewOrders: List[(mutable.Subscriber[NewOrder, mutable.Publisher[NewOrder]])] = delegators.map(new NewOrderSubscriber(_))
  val subscribersNewOrdersCancels: List[mutable.Subscriber[NewOrderCancel, mutable.Publisher[NewOrderCancel]]] = delegators.map(new NewOrderCancelSubscriber(_))

  def init() = {
	subscribersNewOrders.foreach(newOrderPublisher.subscribe)
	subscribersNewOrdersCancels.foreach(cancelOrderPublisher.subscribe)
  }

  init()

  override def handleNewOrder(o: NewOrder): Unit = newOrderPublisher.handle(o)

  override def handleNewOrderCancel(c: NewOrderCancel): Unit = cancelOrderPublisher.handle(c)

  class GenericPublisher[T] extends mutable.Publisher[T] with Handler[T] {

	override def handle(o: T): Unit = publish(o)
  }

  class NewOrderSubscriber(delegate: NewOrderHandler) extends mutable.Subscriber[NewOrder, mutable.Publisher[NewOrder]] {
	override def notify(pub: mutable.Publisher[NewOrder], event: NewOrder): Unit = delegate.handleNewOrder(event)
  }

  class NewOrderCancelSubscriber(delegate: NewOrderHandler) extends mutable.Subscriber[NewOrderCancel, mutable.Publisher[NewOrderCancel]] {
	override def notify(pub: mutable.Publisher[NewOrderCancel], event: NewOrderCancel): Unit = delegate.handleNewOrderCancel(event)
  }


}
