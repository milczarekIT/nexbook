package org.nexbook.repository

import org.nexbook.app.AppConfig
import org.nexbook.core.{Handler, MatchingEngine}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.sequence.SequencerFactory

/**
  * Created by milczu on 09.12.15
  */
class MatchingEnginesRepository(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, orderBookResponseHandlers: List[Handler[OrderBookResponse]]) {

  val matchingEngines = {
	def matchingEngine(symbol: String): MatchingEngine = new MatchingEngine(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers)
	AppConfig.supportedCurrencyPairs.map(symbol => symbol -> matchingEngine(symbol)).toMap
  }

  def find(symbol: String) = matchingEngines.get(symbol).get


}
