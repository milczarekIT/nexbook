package org.nexbook.domain

import org.joda.time.DateTime

trait Order {
  val tradeID: String
  val symbol: String
  val clientId: String
  val size: Double
  val side: Side
  val timestamp: DateTime
  val orderType: OrderType
  var sequence: Long = -1;

  def setSequence(sequence: Long) = if (this.sequence == -1) this.sequence = sequence else throw new IllegalStateException("Sequence already set!")
}

case class MarketOrder(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, timestamp: DateTime, orderType: OrderType) extends Order {

  def this(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, timestamp: DateTime) = this(tradeID, symbol, clientId, side, size, timestamp, Market)
}


case class LimitOrder(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, limit: Double, timestamp: DateTime, orderType: OrderType) extends Order {

  def this(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, limit: Double, timestamp: DateTime) = this(tradeID, symbol, clientId, side, size, limit, timestamp, Limit)
}

object OrderOrdering {
  val timestampDesc = Ordering.fromLessThan[Order](timestampDescCompare(_, _))
  val timestampAsc = Ordering.fromLessThan[Order](timestampAscCompare(_, _))

  val bookBuyOrdering = Ordering.fromLessThan[LimitOrder](bookBuyCompare(_, _))
  val bookSellOrdering = Ordering.fromLessThan[LimitOrder](bookSellCompare(_, _))

  private def timestampDescCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.sequence > o2.sequence
    else o1.timestamp isBefore o2.timestamp
  }

  /**
   * limit DESC
   */
  private def bookBuyCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) timestampAscCompare(o1, o2)
    else o1.limit < o2.limit
  }

  /**
   * limit ASC
   */
  private def bookSellCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) timestampAscCompare(o1, o2)
    else o1.limit > o2.limit
  }

  private def timestampAscCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.sequence > o2.sequence
    else o1.timestamp isAfter o2.timestamp
  }
}
