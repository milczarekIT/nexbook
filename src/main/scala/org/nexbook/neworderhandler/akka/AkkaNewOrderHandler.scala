package org.nexbook.neworderhandler.akka

import akka.actor.{Actor, ActorRef, Props}
import org.nexbook.app.AppConfig
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.NewOrderHandler

/**
  * Created by milczu on 11.12.15
  */
class AkkaNewOrderHandler(delegators: List[NewOrderHandler]) extends NewOrderHandler {

  import org.nexbook.utils.AkkaUtils._

  val symbolBased: Map[String, List[ActorRef]] = {
	AppConfig.supportedCurrencyPairs.map(symbol => symbol -> delegators.map(handler => actorSystem.actorOf(Props(new NewOrderHandlerActorWrapper(handler))))).toMap
  }

  override def handleNewOrder(o: NewOrder): Unit = symbolBased(o.symbol) foreach (_ ! o)

  override def handleNewOrderCancel(c: NewOrderCancel): Unit = symbolBased(c.symbol) foreach (_ ! c)

  class NewOrderHandlerActorWrapper(handler: NewOrderHandler) extends Actor {
	override def receive: Receive = {
	  case o: NewOrder => handler.handleNewOrder(o)
	  case c: NewOrderCancel => handler.handleNewOrderCancel(c)
	}
  }

}
