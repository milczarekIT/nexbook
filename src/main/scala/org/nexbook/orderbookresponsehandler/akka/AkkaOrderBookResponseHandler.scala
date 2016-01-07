package org.nexbook.orderbookresponsehandler.akka

import akka.actor.{ActorRef, Props}
import akka.routing.{RoundRobinRouter, RouterConfig}
import org.nexbook.app.AppConfig
import org.nexbook.concepts.akka.{AkkaHandler, AkkaHandlerWrapper}
import org.nexbook.core.Handler
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse

/**
  * Created by milczu on 11.10.15.
  */
class AkkaOrderBookResponseHandler(delegators: List[Handler[OrderBookResponse]]) extends AkkaHandler[OrderBookResponse] {
  val router: RouterConfig = RoundRobinRouter(AppConfig.roundRobinRouterPool)

  override val actorRefHandlers: List[ActorRef] = delegators.map(handler =>
	actorSystem.actorOf(Props(new AkkaHandlerWrapper[OrderBookResponse](handler)).withRouter(router)))

}