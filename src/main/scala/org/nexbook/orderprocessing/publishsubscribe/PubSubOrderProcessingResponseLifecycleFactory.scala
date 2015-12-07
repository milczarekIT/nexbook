package org.nexbook.orderprocessing.publishsubscribe

import org.nexbook.handler.GeneralResponseHandler
import org.nexbook.orderprocessing.{OrderProcessingResponseHandler, OrderProcessingResponseLifecycleFactory, OrderProcessingResponseSender}

/**
 * Created by milczu on 25.08.15.
 */
class PubSubOrderProcessingResponseLifecycleFactory(generalResponseHandler: GeneralResponseHandler) extends OrderProcessingResponseLifecycleFactory {

  val (publisher, subscriber) = {
    val pub = new OrderProcessingResponsePublisher
    val sub = new OrderProcessingResponseSubscriber(pub, generalResponseHandler)
    pub subscribe sub
    (pub, sub)
  }

  protected override def initializeHandler: OrderProcessingResponseHandler = subscriber

  override def sender: OrderProcessingResponseSender = publisher
}
