package org.nexbook.domain

import org.joda.time.DateTime

/**
  * Created by milczu on 09.12.15
  */
case class OrderCancel(tradeID: Long, timestamp: DateTime, clOrdId: String, order: Order) extends Order {

  val orderType = order.orderType
  val side = order.side
  val qty = order.qty
  val connector = order.connector
  val symbol = order.symbol
  val clientId = order.clientId
  val origClOrdId = order.clOrdId
  override val dealID: Long = order.tradeID

  override def status: OrderStatus = Cancelled

  override def leaveQty = order.leaveQty
}

case class NewOrderCancel(clOrdId: String, origClOrdId: String, connector: String, symbol: String, side: Side) extends NewTradable
