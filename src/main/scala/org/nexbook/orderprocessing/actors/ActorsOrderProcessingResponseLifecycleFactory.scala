package org.nexbook.orderprocessing.actors

import akka.actor.{ActorSystem, Props}
import org.nexbook.handler.GeneralResponseHandler
import org.nexbook.orderprocessing.{OrderProcessingResponseHandler, OrderProcessingResponseLifecycleFactory, OrderProcessingResponseSender}

/**
 * Created by milczu on 11.10.15.
 */
class ActorsOrderProcessingResponseLifecycleFactory(generalResponseHandler: GeneralResponseHandler) extends OrderProcessingResponseLifecycleFactory {

  val system = ActorSystem("OrderProcessingResponseSystem")

  val (notifier, listener) = {
    val listenerRef = system.actorOf(Props[OrderProcessingResponseListener], name = "listener")
    val notifier = new OrderProcessingResponseNotifier(listenerRef)

    (notifier, null)
  }


  protected override def initializeHandler: OrderProcessingResponseHandler = listener

  override def sender: OrderProcessingResponseSender = notifier
}