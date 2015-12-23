package org.nexbook.repository

import org.nexbook.config.ConfigFactory
import org.nexbook.core.OrderBook

object OrderBookRepository {
  val orderBooks: Map[String, OrderBook] = ConfigFactory.supportedCurrencyPairs map (symbol => symbol -> new OrderBook) toMap

  def getOrderBook(symbol: String): OrderBook = orderBooks(symbol)
}
