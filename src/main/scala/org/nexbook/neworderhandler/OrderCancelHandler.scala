package org.nexbook.neworderhandler

import org.nexbook.core.Handler
import org.nexbook.domain.{NewOrderCancel, Order, OrderCancel}
import org.nexbook.repository.{MatchingEnginesRepository, OrderChainedRepository}
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 09.12.15
  */
class OrderCancelHandler(orderRepository: OrderChainedRepository, sequencerFactory: SequencerFactory, clock: Clock, matchingEnginesRepository: MatchingEnginesRepository) extends Handler[NewOrderCancel] {

  import org.nexbook.sequence.SequencerFactory._

  val logger = LoggerFactory.getLogger(classOf[OrderCancelHandler])
  val tradeIDSequencer = sequencerFactory sequencer tradeIDSequencerName

  def handle(newOrderCancel: NewOrderCancel) = {
	logger.debug(s"Handle order cancel: $newOrderCancel")
	def acceptOrder(newOrderCancel: NewOrderCancel, prevOrder: Order): OrderCancel = new OrderCancel(tradeIDSequencer.nextValue, clock.currentDateTime, newOrderCancel.clOrdId, prevOrder)

	orderRepository.findBy(newOrderCancel.origClOrdId, newOrderCancel.connector) match {
	  case Some(order) => matchingEnginesRepository.find(newOrderCancel.symbol).processOrder(acceptOrder(newOrderCancel, order))
	  case None => logger.warn(s"Unable to handle order cancel. ${newOrderCancel.origClOrdId}, connector: ${newOrderCancel.connector}. Original order not found")
	}
  }
}
