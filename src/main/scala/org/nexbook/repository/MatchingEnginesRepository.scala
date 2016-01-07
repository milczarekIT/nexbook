package org.nexbook.repository

import org.nexbook.app.{AppConfig, OrderRepositoryResolver}
import org.nexbook.core.{Handler, MatchingEngine}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.sequence.SequencerFactory

/**
  * Created by milczu on 09.12.15
  */
class MatchingEnginesRepository(orderRepositoryResolver: OrderRepositoryResolver, sequencerFactory: SequencerFactory, orderBookResponseHandlers: List[Handler[OrderBookResponse]], orderChangeHandlers: List[Handler[OrderChangeCommand]]) {

  val matchingEngines = {
	val orderRepository: OrderRepository = orderRepositoryResolver.orderRepository
	def matchingEngine(symbol: String): MatchingEngine = new MatchingEngine(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers, orderChangeHandlers)
	AppConfig.supportedCurrencyPairs.map(symbol => symbol -> matchingEngine(symbol)).toMap
  }

  def find(symbol: String): MatchingEngine = matchingEngines(symbol)

}
