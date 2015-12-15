package org.nexbook.orderprocessing.publishsubscribe

import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.orderprocessing.{ProcessingResponseHandler, ProcessingResponseModule, ProcessingResponseSender}

import scala.collection.mutable

/**
 * Created by milczu on 25.08.15.
 */
class PubSubProcessingResponseModule(handlers: List[ProcessingResponseHandler]) extends ProcessingResponseModule {

  override val responseSender: ProcessingResponseSender = {
    val pub = new ProcessingResponsePublisher
    for(handler <-  handlers) {
      pub subscribe new ProcessingResponseHandlerSubscriberWrapper(handler)
    }
    pub
  }

  class ProcessingResponseHandlerSubscriberWrapper(handler: ProcessingResponseHandler) extends mutable.Subscriber[OrderProcessingResponse, mutable.Publisher[OrderProcessingResponse]] {
    override def notify(pub: mutable.Publisher[OrderProcessingResponse], event: OrderProcessingResponse): Unit = handler handle event
  }
}
