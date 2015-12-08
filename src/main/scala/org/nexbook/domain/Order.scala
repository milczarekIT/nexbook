package org.nexbook.domain

import org.joda.time.DateTime
import org.nexbook.utils.Assert

trait Order extends OrderDetails {

  val tradeID: Long
  val timestamp: DateTime
  private var filledQty: Double = 0.0

  def addFillQty(qty: Double) = {
    Assert.isTrue(qty <= leaveQty)
    this.filledQty += qty
  }

  def leaveQty = qty - filledQty

}

case class MarketOrder(tradeID: Long, symbol: String, clientId: String, side: Side, qty: Double, connector: String, timestamp: DateTime, clOrdId: String, orderType: OrderType = Market) extends Order {

  def this(newOrder: NewMarketOrder, timestamp: DateTime, tradeID: Long) = this(tradeID, newOrder.symbol, newOrder.clientId, newOrder.side, newOrder.qty, newOrder.connector, timestamp, newOrder.clOrdId)
}


case class LimitOrder(tradeID: Long, symbol: String, clientId: String, side: Side, qty: Double, limit: Double, connector: String, timestamp: DateTime, clOrdId: String, orderType: OrderType = Limit) extends Order {

  def this(newOrder: NewLimitOrder, timestamp: DateTime, tradeID: Long) = this(tradeID, newOrder.symbol, newOrder.clientId, newOrder.side, newOrder.qty, newOrder.limit, newOrder.connector, timestamp, newOrder.clOrdId)

}


