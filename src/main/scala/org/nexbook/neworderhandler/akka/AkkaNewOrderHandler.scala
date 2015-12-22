package org.nexbook.neworderhandler.akka

import akka.actor.{ActorRef, Props}
import org.nexbook.concepts.akka.{AkkaHandler, AkkaHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.domain.NewOrder

/**
  * Created by milczu on 11.12.15
  */
class AkkaNewOrderHandler(delegators: List[Handler[NewOrder]]) extends AkkaHandler[NewOrder] {

  override def actorRefHandlers: List[ActorRef] = delegators.map(handler => actorSystem.actorOf(Props(new AkkaHandlerWrapper[NewOrder](handler))))
}
