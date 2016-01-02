package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.{OrderCancelHandler, OrderHandler}
import org.nexbook.orderbookresponsehandler.handler.{FixMessageResponseSender, JsonFileLogger, TradeDatabaseSaver}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.{DbUpdateOrderChangeHandler, OrderChangeCommand}

/**
  * Created by milczu on 12/21/15.
  */
trait DelegatorsProvider extends BasicComponentProvider {

  def orderHandlers: List[Handler[NewOrder]] = List(wire[OrderHandler])

  def orderCancelHandlers: List[Handler[NewOrderCancel]] = List(wire[OrderCancelHandler])

  def orderResponseHandlers: List[Handler[OrderBookResponse]] = {
	AppConfig.runningMode match {
	  case Live => List(wire[JsonFileLogger], wire[TradeDatabaseSaver], wire[FixMessageResponseSender])
	  case Test => List(wire[JsonFileLogger], wire[TradeDatabaseSaver])
	}

  }

  def orderChangeCommandHandlers: List[Handler[OrderChangeCommand]] = List(wire[DbUpdateOrderChangeHandler])
}
