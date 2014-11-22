package org.nexbook.core

import org.nexbook.domain._

class OrderMatcher(book: OrderBook) {

  def apply(order: Order) = order match {
    case o: LimitOrder => None
    case o: MarketOrder => None
  }
}

