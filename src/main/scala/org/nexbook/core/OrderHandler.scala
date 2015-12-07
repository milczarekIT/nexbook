package org.nexbook.core

import org.nexbook.config.ConfigFactory
import org.nexbook.domain.Order
import org.nexbook.orderprocessing.response.OrderValidationRejectionResponse
import org.nexbook.orderprocessing.{OrderProcessingResponseLifecycleFactory, OrderProcessingResponseSender}
import org.nexbook.repository.{OrderDatabaseRepository, OrderBookRepository, OrderRepository}
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

  def handle(order: Order) {
    def onValidationSuccess(order: Order) {
      order.setSequence(sequencer.nextValue)
      order.setTimestamp(clock.getCurrentDateTime)
      logger.debug("Handled order: {} from: " + order.fixId, order)
      orderRepository add order
      orderMatchers.get(order.symbol).get.acceptOrder(order)
    }
    def onValidationError(order: Order, validationError: ValidationError) = orderProcessingSender.send(OrderValidationRejectionResponse(order, validationError.message))

    orderValidator.validate(order) match {
      case None => onValidationSuccess(order)
      case Some(validationError) => onValidationError(order, validationError)
    }
  }


}
