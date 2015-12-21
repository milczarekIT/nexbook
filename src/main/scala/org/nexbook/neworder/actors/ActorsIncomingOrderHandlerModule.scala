package org.nexbook.neworder.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.RoundRobinRouter
import org.nexbook.core.OrderHandler
import org.nexbook.domain.NewOrder
import org.nexbook.neworder.{IncomingOrderHandlerModule, IncomingOrderNotifier}

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
    new ActorIncomingOrderNotifier(system.actorOf(Props(new OrderHandlerActorWrapper).withRouter(RoundRobinRouter(8)), "orderHandlerListener"))
  }
}
