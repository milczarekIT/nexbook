package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain._
import org.nexbook.repository.{OrderBookRepository, OrderRepository}

object App {

  def main(args: Array[String]) {
    println("Start")
    val currencyPairs = List("EUR/USD", "USD/JPY", "GBP/USD", "USD/CAD")
    val orderBookRepository = new OrderBookRepository(currencyPairs)
    val orderRepository = new OrderRepository

    val orderHandler = new OrderHandler(orderBookRepository, orderRepository)

    val orderBuy1 = new LimitOrder(1, "EUR/USD", "Client1", Buy, 100.00, 1.25, DateTime.now(DateTimeZone.UTC))
    val orderBuy2 = new LimitOrder(2, "EUR/USD", "Client2", Buy, 200.00, 1.26, DateTime.now(DateTimeZone.UTC))
    val orderBuy3 = new LimitOrder(3, "EUR/USD", "Client3", Buy, 50.00, 1.24, DateTime.now(DateTimeZone.UTC))
    val orderSell1 = new MarketOrder(4, "PLN/USD", "Client4", Sell, 225.00, DateTime.now(DateTimeZone.UTC))

    //val e = new OrderExecution(orderBuy1, );
    //println("seq: " + e.sequence)
    //println("symbol: " + e.symbol)



    orderHandler handle orderBuy1
    orderHandler handle orderBuy2
    orderHandler handle orderBuy3
    orderHandler handle orderSell1


    println("Finish")
  }
}
