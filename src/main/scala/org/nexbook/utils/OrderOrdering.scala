package org.nexbook.utils

import org.nexbook.domain.{LimitOrder, Order}

/**
 * Created by milczu on 08.12.15.
 */
object OrderOrdering {
  val orderTimestampDesc = Ordering.fromLessThan[Order](timestampDescCompare)
  val orderTradeIDDesc = Ordering.fromLessThan[Order](_.tradeID > _.tradeID)

  private def timestampDescCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.tradeID < o2.tradeID
    else o1.timestamp isAfter o2.timestamp
  }
}

sealed trait BookOrdering {
  protected val tradeIDAsc = Ordering.fromLessThan[Long](_ < _)

  def orderOrdering: Ordering[LimitOrder]

  def priceOrdering: Ordering[Double]
}

case object BuyBookOrdering extends BookOrdering {

  override def orderOrdering: Ordering[LimitOrder] = Ordering.fromLessThan[LimitOrder](compareOrders)

  private def compareOrders(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) tradeIDAsc.lt(o1.tradeID, o2.tradeID)
    else o1.limit > o2.limit
  }

  override def priceOrdering: Ordering[Double] = Ordering.fromLessThan[Double](_ > _)
}

case object SellBookOrdering extends BookOrdering {

  override def orderOrdering: Ordering[LimitOrder] = Ordering.fromLessThan[LimitOrder](compareOrders)

  private def compareOrders(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) tradeIDAsc.lt(o1.tradeID, o2.tradeID)
    else o1.limit < o2.limit
  }

  override def priceOrdering: Ordering[Double] = Ordering.fromLessThan[Double](_ < _)
}
