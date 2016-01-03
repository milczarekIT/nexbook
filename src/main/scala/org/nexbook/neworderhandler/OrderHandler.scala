package org.nexbook.neworderhandler

import java.util.concurrent.atomic.AtomicInteger

import org.nexbook.core.Handler
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.response.{OrderAcceptResponse, OrderBookResponse, OrderValidationRejectionResponse}
import org.nexbook.repository.MatchingEnginesRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.{Clock, OrderValidator, ValidationException}
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}


class OrderHandler(orderBookResponseHandlers: List[Handler[OrderBookResponse]], sequencerFactory: SequencerFactory, clock: Clock, matchingEnginesRepository: MatchingEnginesRepository) extends Handler[NewOrder] {

  val logger = LoggerFactory.getLogger(classOf[OrderHandler])

  import org.nexbook.sequence.SequencerFactory._

  val sequencer = sequencerFactory sequencer tradeIDSequencerName
  val execIDSequencer = sequencerFactory sequencer execIDSequencerName
  val orderValidator = new OrderValidator

  val idGen = new AtomicInteger()


  def handle(newOrder: NewOrder) {
	def onValidationSuccess(order: NewOrder) {
	  val id = idGen.incrementAndGet
	  def acceptOrder(newOrder: NewOrder) = newOrder match {
		case l: NewLimitOrder => new LimitOrder(l, sequencer.nextValue)
		case m: NewMarketOrder => new MarketOrder(m, sequencer.nextValue)
	  }
	  logger.debug(s"$id Handled order SUCCESS: $order from: ${order.connector}")
	  val acceptedOrder = acceptOrder(newOrder)
	  orderBookResponseHandlers.foreach(h => {
		h.handle(OrderAcceptResponse(acceptedOrder))
	  })
	  matchingEnginesRepository.find(newOrder.symbol).processOrder(acceptedOrder)
	}
	def onValidationException(order: NewOrder, e: ValidationException) = {
	  logger.debug(s"Handled order [ValidationException]: $order, validationException: ${e.getMessage}")
	  orderBookResponseHandlers.foreach(_.handle(OrderValidationRejectionResponse(OrderValidationRejection(newOrder, e.getMessage))))
	}


	orderValidator.validate(newOrder) match {
	  case Success(o) => onValidationSuccess(o)
	  case Failure(e: ValidationException) => onValidationException(newOrder, e)
	  case Failure(e) => logger.error("Unexpected exception", e)
	}
  }


}
