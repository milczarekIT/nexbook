package org.nexbook.repository

import org.nexbook.config.ConfigFactory
import org.nexbook.core.{Handler, MatchingEngine}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock

/**
  * Created by milczu on 09.12.15
  */
class MatchingEnginesRepository(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, orderBookResponseHandlers: List[Handler[OrderBookResponse]], clock: Clock) {

  val matchingEngines = initMatchingEngines

  private def initMatchingEngines: Map[String, MatchingEngine] = {
	def matchingEngine(symbol: String): MatchingEngine = new MatchingEngine(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers, clock)
	ConfigFactory.supportedCurrencyPairs.map(symbol => symbol -> matchingEngine(symbol)).toMap
  }

  def find(symbol: String) = matchingEngines.get(symbol).get


}
