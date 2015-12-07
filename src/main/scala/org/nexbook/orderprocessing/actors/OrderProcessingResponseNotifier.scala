package org.nexbook.orderprocessing.actors

import akka.actor.ActorRef
import org.nexbook.orderprocessing.OrderProcessingResponseSender
import org.nexbook.orderprocessing.response.OrderProcessingResponse

/**
 * Created by milczu on 11.10.15.
 */
class OrderProcessingResponseNotifier(listener: ActorRef) extends OrderProcessingResponseSender {

  override def send(response: OrderProcessingResponse) = listener ! response
}
