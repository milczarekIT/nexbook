package org.nexbook.orderprocessing.actors

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Props, ActorRef, Actor, ActorSystem}
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.orderprocessing.{ProcessingResponseHandler, ProcessingResponseModule, ProcessingResponseSender}

/**
 * Created by milczu on 11.10.15.
 */
class ActorsProcessingResponseModule(handlers: List[ProcessingResponseHandler]) extends ProcessingResponseModule {

  override val responseSender: ProcessingResponseSender = {
    val system = ActorSystem("ProcessingResponseSystem")
    val counter = new AtomicInteger

    def actorRef(handler: ProcessingResponseHandler): ActorRef = {
      def props = Props(new ProcessingResponseHandlerActorWrapper(handler))
      def actorName = "ResponseHandler-" + counter.incrementAndGet + "-" + handler.getClass.getSimpleName
      system.actorOf(props, name = actorName)
    }

    new ProcessingResponseNotifier(handlers.map(handler => actorRef(handler)).toList)
  }

  class ProcessingResponseHandlerActorWrapper(handler: ProcessingResponseHandler) extends Actor {
    override def receive: Receive = {
      case a: OrderProcessingResponse => handler.handle(a)
    }
  }
}
