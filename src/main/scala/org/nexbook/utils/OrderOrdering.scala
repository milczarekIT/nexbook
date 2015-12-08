package org.nexbook.utils

import org.nexbook.domain.{LimitOrder, Order}

/**
 * Created by milczu on 08.12.15.
 */
object OrderOrdering {
  val orderTimestampDesc = Ordering.fromLessThan[Order](timestampDescCompare)
  val orderTradeIDDesc = Ordering.fromLessThan[Order](_.tradeID > _.tradeID)

  val tradeIDAsc = Ordering.fromLessThan[Long](_ < _)

  val bookBuyOrdering = Ordering.fromLessThan[LimitOrder](bookBuyCompare)
  val bookSellOrdering = Ordering.fromLessThan[LimitOrder](bookSellCompare)

  private def timestampDescCompare(o1: Order, o2: Order): Boolean = {
    if (o1.timestamp isEqual o2.timestamp) o1.tradeID < o2.tradeID
    else o1.timestamp isAfter o2.timestamp
  }

  /**
   * limit DESC
   */
  private def bookBuyCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) tradeIDAsc.lt(o1.tradeID, o2.tradeID)
    else o1.limit > o2.limit
  }

  /**
   * limit ASC
   */
  private def bookSellCompare(o1: LimitOrder, o2: LimitOrder): Boolean = {
    if (o1.limit == o2.limit) tradeIDAsc.lt(o1.tradeID, o2.tradeID)
    else o1.limit < o2.limit
  }
}
