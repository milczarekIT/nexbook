package org.nexbook.orderprocessing.actors

import akka.actor._
import com.softwaremill.macwire._
import org.nexbook.handler.GeneralResponseHandler
import org.nexbook.orderprocessing.OrderProcessingResponseHandler
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory


/**
 * Created by milczu on 11.10.15.
 */
class OrderProcessingResponseListener(generalResponseHandler: GeneralResponseHandler) extends OrderProcessingResponseHandler with Actor {

  val logger = LoggerFactory.getLogger(classOf[OrderProcessingResponseListener])

  override def receive = {
    case response: OrderProcessingResponse => generalResponseHandler doHandle response
  }

  override def handle(response: OrderProcessingResponse) = receive(response)
}

object OrderProcessingResponseListener {
  def props(generalResponseHandler: GeneralResponseHandler): Props = Props(new OrderProcessingResponseListener(generalResponseHandler))
}
