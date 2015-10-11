package org.nexbook.actors

import akka.actor.{Props, ActorSystem}
import org.nexbook.orderprocessing.{OrderProcessingResponseHandler, OrderProcessingResponseSender, OrderProcessingResponseLifecycleFactory}

/**
 * Created by milczu on 11.10.15.
 */
class ActorsOrderProcessingResponseLifecycleFactory extends OrderProcessingResponseLifecycleFactory {

  val system = ActorSystem("OrderProcessingResponseSystem")

  val (notifier, listener) = {
    val listener = new OrderProcessingResponseListener
    val listenerRef = system.actorOf(Props(listener), name = "listener")

    val notifier = new OrderProcessingResponseNotifier(listenerRef)

    (notifier, listener)
  }


  override def handler: OrderProcessingResponseHandler = listener

  override def sender: OrderProcessingResponseSender = notifier
}
