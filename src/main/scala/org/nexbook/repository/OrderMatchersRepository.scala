package org.nexbook.repository

import org.nexbook.config.ConfigFactory
import org.nexbook.core.OrderMatcher
import org.nexbook.orderprocessing.ProcessingResponseSender
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock

/**
 * Created by milczu on 09.12.15
 */
class OrderMatchersRepository(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, orderProcessingSender: ProcessingResponseSender, clock: Clock) {

  val orderMatchers = initMatchers

  private def initMatchers: Map[String, OrderMatcher] = {
    def orderMatcher(symbol: String): OrderMatcher = new OrderMatcher(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderProcessingSender, clock)
    ConfigFactory.supportedCurrencyPairs.map(symbol => symbol -> orderMatcher(symbol)).toMap
  }

  def find(symbol: String) = orderMatchers.get(symbol).get


}
