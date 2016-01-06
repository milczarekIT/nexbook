package org.nexbook.domain

import org.joda.time.DateTime
import org.nexbook.utils.Assert

trait Trade extends TradeDetails {

  val tradeID: Long
  val timestamp: DateTime
  private var filledQty: Double = 0.0
  private var currentStatus: OrderStatus = New

  def addFillQty(qty: Double) = {
	Assert.isTrue(qty <= leaveQty)
	this.filledQty += qty
  }

  def leaveQty = qty - filledQty

  def dealID: Long

  def updateStatus(newStatus: OrderStatus) = {
	currentStatus = newStatus
  }

  override def status: OrderStatus = currentStatus

}

trait Order extends Trade

case class MarketOrder(tradeID: Long, symbol: String, clientId: String, side: Side, qty: Double, connector: String, timestamp: DateTime, clOrdId: String, orderType: OrderType = Market) extends Order {

  override val dealID: Long = tradeID

  def this(newOrder: NewMarketOrder, tradeID: Long) = this(tradeID, newOrder.symbol, newOrder.clientId, newOrder.side, newOrder.qty, newOrder.connector, newOrder.timestamp, newOrder.clOrdId)
}


case class LimitOrder(tradeID: Long, symbol: String, clientId: String, side: Side, qty: Double, limit: Double, connector: String, timestamp: DateTime, clOrdId: String, orderType: OrderType = Limit) extends Order {

  override val dealID: Long = tradeID

  def this(newOrder: NewLimitOrder, tradeID: Long) = this(tradeID, newOrder.symbol, newOrder.clientId, newOrder.side, newOrder.qty, newOrder.limit, newOrder.connector, newOrder.timestamp, newOrder.clOrdId)
}