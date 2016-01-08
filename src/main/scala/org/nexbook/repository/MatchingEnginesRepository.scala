package org.nexbook.repository

import org.nexbook.app.{AppConfig, OrderRepositoryResolver, PubSub}
import org.nexbook.core.{DefaultMatchingEngine, Handler, MatchingEngine, SynchronizedMatchingEngine}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.sequence.SequencerFactory

/**
  * Created by milczu on 09.12.15
  */
class MatchingEnginesRepository(orderRepositoryResolver: OrderRepositoryResolver, sequencerFactory: SequencerFactory, orderBookResponseHandlers: List[Handler[OrderBookResponse]], orderChangeHandlers: List[Handler[OrderChangeCommand]]) {

  val matchingEngines = {
	val orderRepository: OrderRepository = orderRepositoryResolver.orderRepository
	def matchingEngine(symbol: String): MatchingEngine = {
	  if(AppConfig.mode == PubSub) new DefaultMatchingEngine(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers, orderChangeHandlers) with SynchronizedMatchingEngine
	  else new DefaultMatchingEngine(orderRepository, sequencerFactory, OrderBookRepository.getOrderBook(symbol), orderBookResponseHandlers, orderChangeHandlers)
	}
	AppConfig.supportedCurrencyPairs.map(symbol => symbol -> matchingEngine(symbol)).toMap
  }

  def find(symbol: String): MatchingEngine = matchingEngines(symbol)

}
