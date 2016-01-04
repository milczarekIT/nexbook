package org.nexbook.neworderhandler.akka

import akka.actor.{ActorRef, Props}
import org.nexbook.app.AppConfig
import org.nexbook.concepts.akka.AkkaHandlerWrapper
import org.nexbook.core.Handler
import org.nexbook.domain.NewOrder
import org.nexbook.utils.AkkaUtils

/**
  * Created by milczu on 11.12.15
  */
class AkkaNewOrderHandler(delegators: List[Handler[NewOrder]]) extends Handler[NewOrder] {

  val symbolBased: Map[String, List[ActorRef]] = {
	AppConfig.supportedCurrencyPairs.map(symbol => symbol -> delegators.map(handler => AkkaUtils.actorSystem.actorOf(Props(new AkkaHandlerWrapper[NewOrder](handler))))).toMap
  }

  override def handle(o: NewOrder): Unit = symbolBased(o.symbol) foreach (_ ! o)

}
