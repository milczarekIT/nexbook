package org.nexbook.neworderhandler

import org.nexbook.core.Handler
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.response.{OrderAcceptResponse, OrderBookResponse, OrderValidationRejectionResponse}
import org.nexbook.repository.OrderMatchersRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.{Clock, OrderValidator, ValidationException}
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}


class OrderHandler(orderBookResponseHandlers: List[Handler[OrderBookResponse]], sequencerFactory: SequencerFactory, clock: Clock, orderMatchersRepository: OrderMatchersRepository) extends Handler[NewOrder] {

  val logger = LoggerFactory.getLogger(classOf[OrderHandler])

  import org.nexbook.sequence.SequencerFactory._

  val sequencer = sequencerFactory sequencer tradeIDSequencerName
  val execIDSequencer = sequencerFactory sequencer execIDSequencerName
  val orderValidator = new OrderValidator


  def handle(newOrder: NewOrder) {
	def onValidationSuccess(order: NewOrder) {
	  def acceptOrder(newOrder: NewOrder) = newOrder match {
		case l: NewLimitOrder => new LimitOrder(l, sequencer.nextValue)
		case m: NewMarketOrder => new MarketOrder(m, sequencer.nextValue)
	  }
	  logger.debug("Handled order: {} from: " + order.connector, order)
	  val acceptedOrder = acceptOrder(newOrder)
	  orderBookResponseHandlers.foreach(_.handle(OrderAcceptResponse(acceptedOrder)))
	  orderMatchersRepository.find(newOrder.symbol).processOrder(acceptedOrder)
	}
	def onValidationException(order: NewOrder, e: ValidationException) = {
	  orderBookResponseHandlers.foreach(_.handle(OrderValidationRejectionResponse(OrderValidationRejection(newOrder, e.getMessage))))
	}


	orderValidator.validate(newOrder) match {
	  case Success(o) => onValidationSuccess(o)
	  case Failure(e: ValidationException) => onValidationException(newOrder, e)
	  case Failure(e) => logger.error("Unexpected exception", e)
	}
  }


}
