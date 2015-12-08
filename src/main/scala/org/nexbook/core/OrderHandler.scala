package org.nexbook.core

import org.nexbook.config.ConfigFactory
import org.nexbook.domain._
import org.nexbook.orderprocessing.response.OrderValidationRejectionResponse
import org.nexbook.orderprocessing.{OrderProcessingResponseLifecycleFactory, OrderProcessingResponseSender}
import org.nexbook.repository.{OrderBookRepository, OrderDatabaseRepository}
import org.nexbook.utils.{Clock, OrderValidator, ValidationError}
import org.slf4j.LoggerFactory


class OrderHandler(orderBookRepository: OrderBookRepository, orderRepository: OrderDatabaseRepository, orderProcessingResponseLifecycleFactory: OrderProcessingResponseLifecycleFactory, clock: Clock) {
  val logger = LoggerFactory.getLogger(classOf[OrderHandler])
  val sequencer = new Sequencer
  val execIDSequencer = new Sequencer
  val orderValidator = new OrderValidator
  val orderProcessingSender: OrderProcessingResponseSender = orderProcessingResponseLifecycleFactory.sender

  val orderMatchers = initMatchers

  def initMatchers: Map[String, OrderMatcher] = {
    def orderMatcher(symbol: String): OrderMatcher = new OrderMatcher(execIDSequencer, orderBookRepository.getOrderBook(symbol), orderProcessingSender, clock)

    ConfigFactory.supportedCurrencyPairs.map(symbol => symbol -> orderMatcher(symbol)).toMap
  }

  def acceptOrder(newOrder: NewOrder) = newOrder match {
    case l: NewLimitOrder => new LimitOrder(l, clock.getCurrentDateTime, sequencer.nextValue)
    case m: NewMarketOrder => new MarketOrder(m, clock.getCurrentDateTime, sequencer.nextValue)
  }

  def handle(newOrder: NewOrder) {
    def onValidationSuccess(order: NewOrder) {
      logger.debug("Handled order: {} from: " + order.connector, order)
      val acceptedOrder = acceptOrder(newOrder)
      orderRepository add acceptedOrder
      orderMatchers.get(order.symbol).get.processOrder(acceptedOrder)
    }
    def onValidationError(order: NewOrder, validationError: ValidationError) = orderProcessingSender.send(OrderValidationRejectionResponse(newOrder, validationError.message))

    orderValidator.validate(newOrder) match {
      case None => onValidationSuccess(newOrder)
      case Some(validationError) => onValidationError(newOrder, validationError)
    }
  }


}
