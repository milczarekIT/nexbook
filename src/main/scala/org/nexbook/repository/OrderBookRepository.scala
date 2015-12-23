package org.nexbook.repository

import org.nexbook.app.AppConfig
import org.nexbook.core.OrderBook

object OrderBookRepository {
  val orderBooks: Map[String, OrderBook] = AppConfig.supportedCurrencyPairs map (symbol => symbol -> new OrderBook) toMap

  def getOrderBook(symbol: String): OrderBook = orderBooks(symbol)
}
