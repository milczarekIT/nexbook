package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain.Order
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.slf4j.LoggerFactory


class OrderHandler(sequencer: Sequencer, orderBookRepository: OrderBookRepository, orderRepository: OrderRepository, executionHandler: ExecutionHandler) {
  val LOGGER = LoggerFactory.getLogger(classOf[OrderHandler])

  val orderMatchers = initMatchers

  def initMatchers: Map[String, OrderMatcher] = orderBookRepository.getSymbols.map {
    symbol =>
      val orderMatcher = new OrderMatcher(orderBookRepository.getOrderBook(symbol));
      orderMatcher.subscribe(executionHandler)
      symbol -> orderMatcher
  }.toMap

  def handle(order: Order) {
    if (validate(order)) {
      order.setSequence(sequencer.nextValue)
      order.setTimestamp(DateTime.now(DateTimeZone.UTC));
      LOGGER.debug("Handled order: {}", order)
      orderRepository add order
      orderMatchers.get(order.symbol).get.acceptOrder(order)
    } else {
      println("Order rejected: " + order)
    }
  }

  private def validate(order: Order): Boolean = orderMatchers.contains(order.symbol)


}
