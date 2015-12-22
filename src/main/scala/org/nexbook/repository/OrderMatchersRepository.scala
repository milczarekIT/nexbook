package org.nexbook.repository

import org.nexbook.config.ConfigFactory
import org.nexbook.core.{Handler, OrderMatcher}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock

/**
 * Created by milczu on 09.12.15
 */
class OrderMatchersRepository(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, orderBookResponseHandlers: List[Handler[OrderBookResponse]], clock: Clock) {

  val orderMatchers = initMatchers

  private def initMatchers: Map[String, OrderMatcher] = {
    def orderMatcher(symbol: String): OrderMatcher = new OrderMatcher(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers, clock)
    ConfigFactory.supportedCurrencyPairs.map(symbol => symbol -> orderMatcher(symbol)).toMap
  }

  def find(symbol: String) = orderMatchers.get(symbol).get


}
