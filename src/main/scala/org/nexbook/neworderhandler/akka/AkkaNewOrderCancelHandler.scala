package org.nexbook.neworderhandler.akka

import akka.actor.{ActorRef, Props}
import akka.routing.RoundRobinRouter
import org.nexbook.concepts.akka.{AkkaHandler, AkkaHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.domain.NewOrderCancel

/**
  * Created by milczu on 12/21/15.
  */
class AkkaNewOrderCancelHandler(delegators: List[Handler[NewOrderCancel]]) extends AkkaHandler[NewOrderCancel] {

  override def actorRefHandlers: List[ActorRef] = delegators.map(handler => actorSystem.actorOf(Props(new AkkaHandlerWrapper[NewOrderCancel](handler)).withRouter(RoundRobinRouter(1))))

}
