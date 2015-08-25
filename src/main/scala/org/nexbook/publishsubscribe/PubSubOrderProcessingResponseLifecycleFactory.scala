package org.nexbook.publishsubscribe

import org.nexbook.orderprocessing.{OrderProcessingResponseHandler, OrderProcessingResponseSender, OrderProcessingResponseLifecycleFactory}

/**
 * Created by milczu on 25.08.15.
 */
class PubSubOrderProcessingResponseLifecycleFactory extends OrderProcessingResponseLifecycleFactory {

  val (publisher, subscriber) = initialize

  def initialize = {
    val pub = new OrderProcessingResponsePublisher
    val sub = new OrderProcessingResponseSubscriber(pub)
    pub.subscribe(sub)
    (pub, sub)
  }

  override def handler: OrderProcessingResponseHandler = subscriber

  override def sender: OrderProcessingResponseSender = publisher
}
