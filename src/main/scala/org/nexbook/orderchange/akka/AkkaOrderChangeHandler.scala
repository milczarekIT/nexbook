package org.nexbook.orderchange.akka

import akka.actor.{ActorRef, Props}
import akka.routing.RoundRobinRouter
import org.nexbook.concepts.akka.{AkkaHandler, AkkaHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.orderchange.OrderChangeCommand

/**
  * Created by milczu on 12/23/15.
  */
class AkkaOrderChangeHandler(delegators: List[Handler[OrderChangeCommand]]) extends AkkaHandler[OrderChangeCommand] {
  override def actorRefHandlers: List[ActorRef] = delegators.map(handler => actorSystem.actorOf(Props(new AkkaHandlerWrapper[OrderChangeCommand](handler)).withRouter(RoundRobinRouter(32))))
}
