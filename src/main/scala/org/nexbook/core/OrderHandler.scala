package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.config.ConfigFactory
import org.nexbook.domain.Order
import org.nexbook.orderprocessing.response.OrderValidationRejectionResponse
import org.nexbook.orderprocessing.{OrderProcessingResponseSender, OrderProcessingResponseLifecycleFactory}
import org.nexbook.publishsubscribe.OrderProcessingResponseSubscriber
import org.nexbook.repository.{OrderBookRepository, OrderRepository}
import org.nexbook.utils.{ValidationError, OrderValidator}
import org.slf4j.LoggerFactory


class OrderHandler(orderBookRepository: OrderBookRepository, orderRepository: OrderRepository, orderProcessingResponseLifecycleFactory: OrderProcessingResponseLifecycleFactory) {
  val logger = LoggerFactory.getLogger(classOf[OrderHandler])
  val sequencer = new Sequencer
  val orderValidator = new OrderValidator
  val orderProcessingSender: OrderProcessingResponseSender = orderProcessingResponseLifecycleFactory.sender

  val orderMatchers = initMatchers

  def initMatchers: Map[String, OrderMatcher] = {
    def orderMatcher(symbol: String):OrderMatcher = new OrderMatcher(orderBookRepository.getOrderBook(symbol), orderProcessingSender)

    ConfigFactory.supportedCurrencyPairs.map(symbol => symbol -> orderMatcher(symbol)).toMap
  }

  def handle(order: Order, sender: String) {
    def onValidationSuccess(order: Order) {
      order.setSequence(sequencer.nextValue)
      order.setTimestamp(DateTime.now(DateTimeZone.UTC))
      logger.debug("Handled order: {} from: " + sender, order)
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
