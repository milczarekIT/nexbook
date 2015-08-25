package org.nexbook.repository

import org.nexbook.config.ConfigFactory
import org.nexbook.core.OrderBook

class OrderBookRepository {
  val orderBooks: Map[String, OrderBook] = init

  private def init: Map[String, OrderBook] = {
    ConfigFactory.supportedCurrencyPairs map (symbol => symbol -> new OrderBook) toMap
  }

  def getOrderBook(symbol: String): OrderBook = orderBooks(symbol)
}
