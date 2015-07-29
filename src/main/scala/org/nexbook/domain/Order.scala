package org.nexbook.domain

import org.joda.time.DateTime
import org.nexbook.utils.Assert

trait Order {

  val tradeID: String
  val symbol: String
  val clientId: String
  val size: Double
  val side: Side
  val orderType: OrderType
  private var timestampVal: DateTime = new DateTime(0)
  private var fillSize: Double = 0.0
  private var sequenceVal: Long = -1;

  def setSequence(sequence: Long) = if (this.sequenceVal == -1) this.sequenceVal = sequence else throw new IllegalStateException("Sequence already set!")

  def sequence = sequenceVal

  def addFillSize(fill: Double) = {
    Assert.isTrue(fill <= remainingSize)
    this.fillSize += fill
  }

  def remainingSize = size - fillSize

  def setTimestamp(timestamp: DateTime) = this.timestampVal = timestamp

  def timestamp = timestampVal
}

case class MarketOrder(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, orderType: OrderType) extends Order {

  def this(tradeID: String, symbol: String, clientId: String, side: Side, size: Double) = this(tradeID, symbol, clientId, side, size, Market)
}


case class LimitOrder(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, limit: Double, orderType: OrderType) extends Order {

  def this(tradeID: String, symbol: String, clientId: String, side: Side, size: Double, limit: Double) = this(tradeID, symbol, clientId, side, size, limit, Limit)

}

object OrderOrdering {
  val timestampDesc = Ordering.fromLessThan[Order](timestampDescCompare(_, _))
  val timestampAsc = Ordering.fromLessThan[Order](timestampAscCompare(_, _))

  val sequenceAsc = Ordering.fromLessThan[Long](_ < _)

  val bookBuyOrdering = Ordering.fromLessThan[LimitOrder](bookBuyCompare(_, _))
  val bookSellOrdering = Ordering.fromLessThan[LimitOrder](bookSellCompare(_, _))

  private def timestampDescCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.sequence < o2.sequence
    else o1.timestamp isAfter o2.timestamp
  }

  private def timestampAscCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.sequence < o2.sequence
    else o1.timestamp isBefore o2.timestamp
  }

  /**
   * limit DESC
   */
  private def bookBuyCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) sequenceAsc.lt(o1.sequence, o2.sequence)
    else o1.limit > o2.limit
  }

  /**
   * limit ASC
   */
  private def bookSellCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) sequenceAsc.lt(o1.sequence, o2.sequence)
    else o1.limit < o2.limit
  }
}
