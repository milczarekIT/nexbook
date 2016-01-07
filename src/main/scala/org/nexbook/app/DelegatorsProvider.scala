package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.core.Handler
import org.nexbook.neworderhandler.{NewOrderHandler, OrderHandler}
import org.nexbook.orderbookresponsehandler.handler.{FixMessageResponseSender, JsonFileLogger, TradeDatabaseSaver}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.{DbUpdateOrderChangeHandler, OrderChangeCommand}

/**
  * Created by milczu on 12/21/15.
  */
trait DelegatorsProvider extends BasicComponentProvider {

  def orderHandlers: List[NewOrderHandler] = List(wire[OrderHandler])

  def orderResponseHandlers: List[Handler[OrderBookResponse]] = {
	var handlers: List[Handler[OrderBookResponse]] = List(wire[JsonFileLogger])
	if(AppConfig.dbPersist) {
	  handlers = wire[TradeDatabaseSaver] :: handlers
	}
	if(AppConfig.runningMode == Live) {
	  handlers = wire[FixMessageResponseSender] :: handlers
	}
	handlers.reverse
  }

  def orderChangeCommandHandlers: List[Handler[OrderChangeCommand]] = if(AppConfig.dbPersist) List(wire[DbUpdateOrderChangeHandler]) else  List()
}
