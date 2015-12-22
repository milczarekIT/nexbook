package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain.{Buy, LimitOrder, Sell}
import org.scalatest._

class OrderBookTest extends FlatSpec with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  "Empty orderBook" should "return None Order on top" in {
	val orderBook = new OrderBook

	orderBook.top(Buy) shouldBe None
	orderBook.top(Sell) shouldBe None
  }

  "Empty orderBook" should "not throw any exception" in {
	val orderBook = new OrderBook

	orderBook.removeTop(Buy)
	orderBook.removeTop(Sell)

	orderBook.top(Buy) shouldBe None
	orderBook.top(Sell) shouldBe None
  }

  "Not empty orderBook" should "remove first order with removeTop" in {
	val orderBook = new OrderBook

	val order1 = new LimitOrder(1, "EUR/CHF", "CID", Sell, 2000.00, 2.95, "NEX", now, "TID_1")
	val order2 = new LimitOrder(2, "EUR/CHF", "CID", Sell, 2000.00, 2.96, "NEX", now, "TID_2")
	orderBook add order1
	orderBook add order2

	orderBook.top(Sell) should contain(order1)

	orderBook removeTop Sell

	orderBook.top(Sell) should contain(order2)

	orderBook removeTop Sell

	orderBook.top(Sell) shouldBe None
  }

  "Sell OrderBook top" should "return LimitOrder with the lowest limit price" in {
	val orderBook = new OrderBook

	val limitOrder1 = new LimitOrder(1, "EUR/CHF", "CID", Sell, 2000.00, 2.95, "NEX", now, "TID_1")
	orderBook add limitOrder1
	orderBook.top(Sell) should contain(limitOrder1)

	val limitOrder2 = new LimitOrder(2, "EUR/CHF", "CID", Sell, 2000.00, 2.92, "NEX", now, "TID_2")
	orderBook add limitOrder2
	orderBook.top(Sell) should contain(limitOrder2)

	val limitOrder3 = new LimitOrder(3, "EUR/CHF", "CID", Sell, 2000.00, 2.91, "NEX", now, "TID_3")
	orderBook add limitOrder3
	orderBook.top(Sell) should contain(limitOrder3)


	val limitOrder4 = new LimitOrder(4, "EUR/CHF", "CID", Sell, 2000.00, 2.99, "NEX", now, "TID_4")

	orderBook add limitOrder4
	orderBook.top(Sell) should contain(limitOrder3)
  }

  "Sell OrderBook top with same lowest limit price" should "return order with lowest sequence" in {
	val orderBook = new OrderBook


	val limitOrder1 = new LimitOrder(8, "EUR/CHF", "CID", Sell, 2000.00, 2.95, "NEX", now, "TID_1")
	val limitOrder2 = new LimitOrder(1, "EUR/CHF", "CID", Sell, 2000.00, 2.95, "NEX", now, "TID_2")
	val limitOrder3 = new LimitOrder(2, "EUR/CHF", "CID", Sell, 2000.00, 2.95, "NEX", now, "TID_3")
	orderBook add limitOrder1
	orderBook add limitOrder2
	orderBook add limitOrder3

	orderBook.top(Sell) should contain(limitOrder2)
  }

  "Buy OrderBook top" should "return LimitOrder with the highest limit price" in {
	val orderBook = new OrderBook

	val limitOrder1 = new LimitOrder(1, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_1")
	orderBook add limitOrder1
	orderBook.top(Buy) should contain(limitOrder1)

	val limitOrder2 = new LimitOrder(2, "EUR/CHF", "CID", Buy, 2000.00, 2.96, "NEX", now, "TID_2")
	orderBook add limitOrder2
	orderBook.top(Buy) should contain(limitOrder2)

	val limitOrder3 = new LimitOrder(3, "EUR/CHF", "CID", Buy, 2000.00, 2.97, "NEX", now, "TID_3")
	orderBook add limitOrder3
	orderBook.top(Buy) should contain(limitOrder3)


	val limitOrder4 = new LimitOrder(4, "EUR/CHF", "CID", Buy, 2000.00, 2.91, "NEX", now, "TID_4")

	orderBook add limitOrder4
	orderBook.top(Buy) should contain(limitOrder3)
  }

  "Buy OrderBook top with same the highest limit price" should "return order with lowest sequence" in {
	val orderBook = new OrderBook

	val limitOrder1 = new LimitOrder(8, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_1")
	val limitOrder2 = new LimitOrder(1, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_2")
	val limitOrder3 = new LimitOrder(2, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_3")
	orderBook add limitOrder1
	orderBook add limitOrder2
	orderBook add limitOrder3

	orderBook.top(Buy) should contain(limitOrder2)
	orderBook.top(Buy) shouldNot contain(limitOrder1)
  }

  "find order by side and tradeID" should "return None for empty OrderBook" in {
	val orderBook = new OrderBook

	val orderOption = orderBook.find(Buy, 1)

	orderOption shouldBe None
  }

  "find order by side and tradeID" should "return None for not matching side" in {
	val orderBook = new OrderBook

	val limitOrder1 = new LimitOrder(1, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_2")
	orderBook add limitOrder1

	val orderOption = orderBook.find(Sell, 1)

	orderOption shouldBe None
  }

  "find order by side and tradeID" should "return Some(order) for matching tradeID" in {
	val orderBook = new OrderBook

	val limitOrder1 = new LimitOrder(8, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_1")
	val limitOrder2 = new LimitOrder(1, "EUR/CHF", "CID", Buy, 2000.00, 2.95, "NEX", now, "TID_2")
	orderBook add limitOrder1
	orderBook add limitOrder2

	val orderOption = orderBook.find(Buy, 1)

	orderOption shouldBe Some(limitOrder2)
  }
}
