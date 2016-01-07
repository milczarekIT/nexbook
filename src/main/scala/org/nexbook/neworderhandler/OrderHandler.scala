package org.nexbook.neworderhandler

import org.nexbook.app.OrderRepositoryResolver
import org.nexbook.core.Handler
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.response.{OrderAcceptResponse, OrderBookResponse, OrderValidationRejectionResponse}
import org.nexbook.repository.MatchingEnginesRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.{Clock, NewOrderValidator, ValidationException}
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}


class OrderHandler(orderRepositoryResolver: OrderRepositoryResolver, orderBookResponseHandlers: List[Handler[OrderBookResponse]], sequencerFactory: SequencerFactory, clock: Clock, matchingEnginesRepository: MatchingEnginesRepository) extends NewOrderHandler {

  val logger = LoggerFactory.getLogger(classOf[OrderHandler])

  import org.nexbook.sequence.SequencerFactory._

  val sequencer = sequencerFactory sequencer tradeIDSequencerName
  val orderValidator = new NewOrderValidator
  val orderRepository = orderRepositoryResolver.orderRepository

  override def handleNewOrder(newOrder: NewOrder) {
	def onValidationSuccess(order: NewOrder) {
	  def acceptOrder(newOrder: NewOrder) = newOrder match {
		case l: NewLimitOrder => new LimitOrder(l, sequencer.nextValue)
		case m: NewMarketOrder => new MarketOrder(m, sequencer.nextValue)
	  }
	  logger.debug(s"Handled order SUCCESS: $order from: ${order.connector}")
	  val acceptedOrder = acceptOrder(newOrder)
	  orderBookResponseHandlers.foreach(_.handle(OrderAcceptResponse(acceptedOrder)))
	  orderRepository add acceptedOrder
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

  override def handleNewOrderCancel(newOrderCancel: NewOrderCancel) = {
	logger.debug(s"Handle order cancel: $newOrderCancel")
	def acceptOrder(newOrderCancel: NewOrderCancel, prevOrder: Order): OrderCancel = new OrderCancel(sequencer.nextValue, clock.currentDateTime, newOrderCancel.clOrdId, prevOrder)

	orderRepository.findByClOrdId(newOrderCancel.origClOrdId) match {
	  case Some(order) =>
		val acceptedCancel = acceptOrder(newOrderCancel, order)
		orderRepository add acceptedCancel
		matchingEnginesRepository.find(newOrderCancel.symbol).processOrder(acceptedCancel)
	  case None => logger.warn(s"Unable to handle order cancel. ${newOrderCancel.origClOrdId}, connector: ${newOrderCancel.connector}. Original order not found")
	}
  }


}
