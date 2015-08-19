package org.nexbook.repository

import org.nexbook.core.OrderBook

class OrderBookRepository {
  val SUPPORTED_CURRENCY_PAIRS = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")

  val orderBooks: Map[String, OrderBook] = init

  private def init: Map[String, OrderBook] = {
    SUPPORTED_CURRENCY_PAIRS map (symbol => symbol -> new OrderBook) toMap
  }

  def getOrderBook(symbol: String): OrderBook = orderBooks(symbol)


  def getSymbols: List[String] = SUPPORTED_CURRENCY_PAIRS;

}
