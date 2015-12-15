package org.nexbook.neworder.actors

import akka.actor.{Props, Actor, ActorSystem}
import org.nexbook.core.OrderHandler
import org.nexbook.domain.NewOrder
import org.nexbook.neworder.{IncomingOrderNotifier, IncomingOrderHandlerModule}

/**
 * Created by milczu on 11.12.15
 */
class ActorsIncomingOrderHandlerModule(orderHandler: OrderHandler) extends IncomingOrderHandlerModule {

  val system = ActorSystem("IncomingOrderHandlerSystem")

  override def incomingOrderNotifier: IncomingOrderNotifier = {
    class OrderHandlerActorWrapper extends Actor {
      override def receive: Receive = {
        case order: NewOrder => orderHandler handle order
      }
    }
    new ActorIncomingOrderNotifier(system.actorOf(Props(new OrderHandlerActorWrapper), "orderHandlerListener"))
  }
}
