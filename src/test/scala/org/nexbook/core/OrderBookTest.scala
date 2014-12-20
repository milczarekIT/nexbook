package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain.{Buy, LimitOrder, Sell}
import org.scalatest._

class OrderBookTest extends FlatSpec with Matchers {

  "Empty orderBook" should "return nil Order on top" in {
    val orderBook = new OrderBook

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)
  }

  "Sell OrderBook top" should "return LimitOrder with the lowest limit price" in {
    val orderBook = new OrderBook

    val limitOrder1 = new LimitOrder("TID_1", "EUR/CHF", "CID", Sell, 2000.00, 2.95, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder1
    orderBook.top(Sell).get should be(limitOrder1)

    val limitOrder2 = new LimitOrder("TID_2", "EUR/CHF", "CID", Sell, 2000.00, 2.92, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder2
    orderBook.top(Sell).get should be(limitOrder2)

    val limitOrder3 = new LimitOrder("TID_3", "EUR/CHF", "CID", Sell, 2000.00, 2.91, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder3
    orderBook.top(Sell).get should be(limitOrder3)


    val limitOrder4 = new LimitOrder("TID_4", "EUR/CHF", "CID", Sell, 2000.00, 2.99, DateTime.now(DateTimeZone.UTC))

    orderBook add limitOrder4
    orderBook.top(Sell).get should be(limitOrder3)
  }

  "Sell OrderBook top with same lowest limit price" should "return order with lowest sequence" in {
    val orderBook = new OrderBook
    val now = DateTime.now(DateTimeZone.UTC)


    val limitOrder1 = new LimitOrder("TID_1", "EUR/CHF", "CID", Sell, 2000.00, 2.95, now)
    limitOrder1.setSequence(8)
    val limitOrder2 = new LimitOrder("TID_2", "EUR/CHF", "CID", Sell, 2000.00, 2.95, now)
    limitOrder2.setSequence(1)
    val limitOrder3 = new LimitOrder("TID_3", "EUR/CHF", "CID", Sell, 2000.00, 2.95, now)
    limitOrder3.setSequence(2)
    orderBook add limitOrder1
    orderBook add limitOrder2
    orderBook add limitOrder3

    orderBook.top(Sell).get should be(limitOrder2)
  }

  "Buy OrderBook top" should "return LimitOrder with the highest limit price" in {
    val orderBook = new OrderBook

    val limitOrder1 = new LimitOrder("TID_1", "EUR/CHF", "CID", Buy, 2000.00, 2.95, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder1
    orderBook.top(Buy).get should be(limitOrder1)

    val limitOrder2 = new LimitOrder("TID_2", "EUR/CHF", "CID", Buy, 2000.00, 2.96, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder2
    orderBook.top(Buy).get should be(limitOrder2)

    val limitOrder3 = new LimitOrder("TID_3", "EUR/CHF", "CID", Buy, 2000.00, 2.97, DateTime.now(DateTimeZone.UTC))
    orderBook add limitOrder3
    orderBook.top(Buy).get should be(limitOrder3)


    val limitOrder4 = new LimitOrder("TID_4", "EUR/CHF", "CID", Buy, 2000.00, 2.91, DateTime.now(DateTimeZone.UTC))

    orderBook add limitOrder4
    orderBook.top(Buy).get should be(limitOrder3)
  }

  "Buy OrderBook top with same hihest limit price" should "return order with lowest sequence" in {
    val orderBook = new OrderBook
    val now = DateTime.now(DateTimeZone.UTC)


    val limitOrder1 = new LimitOrder("TID_1", "EUR/CHF", "CID", Buy, 2000.00, 2.95, now)
    limitOrder1.setSequence(8)
    val limitOrder2 = new LimitOrder("TID_2", "EUR/CHF", "CID", Buy, 2000.00, 2.95, now)
    limitOrder2.setSequence(1)
    val limitOrder3 = new LimitOrder("TID_3", "EUR/CHF", "CID", Buy, 2000.00, 2.95, now)
    limitOrder3.setSequence(2)
    orderBook add limitOrder1
    orderBook add limitOrder2
    orderBook add limitOrder3

    orderBook.top(Buy).get should be(limitOrder2)
  }
}
