package org.nexbook.orderprocessing.actors

import akka.actor.ActorRef
import org.nexbook.orderprocessing.ProcessingResponseSender
import org.nexbook.orderprocessing.response.OrderProcessingResponse

/**
 * Created by milczu on 11.10.15.
 */
class ProcessingResponseNotifier(listeners: List[ActorRef]) extends ProcessingResponseSender {

  override def send(response: OrderProcessingResponse) = listeners.foreach(_ ! response)
}
