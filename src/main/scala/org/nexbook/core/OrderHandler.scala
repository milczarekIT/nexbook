package org.nexbook.core

import org.nexbook.domain.Order
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.nexbook.utils.Assert
import org.slf4j.LoggerFactory


class OrderHandler(sequencer: Sequencer, orderBookRepository: OrderBookRepository, orderRepository: OrderRepository) {
  val LOGGER = LoggerFactory.getLogger(classOf[OrderHandler])

  val orderMatchers = initMatchers

  def initMatchers: Map[String, OrderMatcher] = orderBookRepository.getSymbols.map(symbol => symbol -> new OrderMatcher(orderBookRepository.getOrderBook(symbol))).toMap

  def handle(order: Order) {
    Assert.isTrue(orderMatchers.contains(order.symbol))
    order.setSequence(sequencer.nextValue)
    LOGGER.debug("Handled order: {}", order)
    orderRepository add order


    orderMatchers.get(order.symbol).get.apply(order)
  }
}
