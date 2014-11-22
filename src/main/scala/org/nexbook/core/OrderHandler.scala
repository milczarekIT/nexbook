package org.nexbook.core

import org.nexbook.domain.{Buy, Order, Sell}
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.nexbook.utils.Assert


class OrderHandler(orderBookRepository: OrderBookRepository, orderRepository: OrderRepository) {
  val orderMatchers = initMatchers

  def initMatchers: Map[String, OrderMatcher] = orderBookRepository.getSymbols.map(symbol => symbol -> new OrderMatcher(orderBookRepository.getOrderBook(symbol))).toMap

  def handle(order: Order) {
    Assert.isTrue(orderMatchers.contains(order.symbol))
    orderRepository add order


    orderMatchers.get(order.symbol).get.apply(order)
  }
}
