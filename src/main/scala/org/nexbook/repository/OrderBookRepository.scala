package org.nexbook.repository

import org.nexbook.core.OrderBook
import org.nexbook.domain.{Buy, Sell, Side}

class OrderBookRepository(symbols: List[String]) {
  val orderBooks = init()

  def init(): Map[String, OrderBook] = {

    symbols map (symbol => symbol -> new OrderBook) toMap
  }

  def getOrderBook(symbol: String): OrderBook = orderBooks(symbol)


  def getSymbols: List[String] = symbols;

}
