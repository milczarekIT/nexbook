package org.nexbook.core

import org.nexbook.domain._
import org.nexbook.orderprocessing.ProcessingResponseSender
import org.nexbook.orderprocessing.response.{OrderAcceptResponse, OrderValidationRejectionResponse}
import org.nexbook.repository.OrderMatchersRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.{Clock, OrderValidator, ValidationError}
import org.slf4j.LoggerFactory


class OrderHandler(orderProcessingSender: ProcessingResponseSender, sequencerFactory: SequencerFactory, clock: Clock, orderMatchersRepository: OrderMatchersRepository) extends NewTradableHandler[NewOrder] {

  val logger = LoggerFactory.getLogger(classOf[OrderHandler])

  import org.nexbook.sequence.SequencerFactory._

  val sequencer = sequencerFactory sequencer tradeIDSequencerName
  val execIDSequencer = sequencerFactory sequencer execIDSequencerName
  val orderValidator = new OrderValidator


  override def handle(newOrder: NewOrder) {
    def onValidationSuccess(order: NewOrder) {
      def acceptOrder(newOrder: NewOrder) = newOrder match {
        case l: NewLimitOrder => new LimitOrder(l, sequencer.nextValue)
        case m: NewMarketOrder => new MarketOrder(m, sequencer.nextValue)
      }
      logger.debug("Handled order: {} from: " + order.connector, order)
      val acceptedOrder = acceptOrder(newOrder)
      orderProcessingSender.send(OrderAcceptResponse(acceptedOrder))
      orderMatchersRepository.find(newOrder.symbol).processOrder(acceptedOrder)
    }
    def onValidationError(order: NewOrder, validationError: ValidationError) = orderProcessingSender.send(OrderValidationRejectionResponse(OrderValidationRejection(newOrder, validationError.message)))

    orderValidator.validate(newOrder) match {
      case None => onValidationSuccess(newOrder)
      case Some(validationError) => onValidationError(newOrder, validationError)
    }
  }


}
