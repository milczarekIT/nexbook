package org.nexbook.core

import org.nexbook.domain.{Sell, Buy}
import org.scalatest._

class OrderBookTest extends FlatSpec with Matchers {

  "Empty orderBook" should "return nil Order on top" in {
    val orderBook = new OrderBook

    orderBook.top(Buy) should be (None)
    orderBook.top(Sell) should be (None)
  }
}
