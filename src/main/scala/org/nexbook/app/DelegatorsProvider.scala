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
	AppConfig.runningMode match {
	  case Live => List(wire[JsonFileLogger], wire[TradeDatabaseSaver], wire[FixMessageResponseSender])
	  case Test => List(wire[JsonFileLogger], wire[TradeDatabaseSaver])
	}
  }

  def orderChangeCommandHandlers: List[Handler[OrderChangeCommand]] = List(wire[DbUpdateOrderChangeHandler])
}
