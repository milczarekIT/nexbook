package org.nexbook.core

import org.nexbook.domain.{NewOrderCancel, Order, OrderCancel}
import org.nexbook.repository.{OrderChainedRepository, OrderMatchersRepository}
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 09.12.15
 */
class OrderCancelHandler(orderRepository: OrderChainedRepository, sequencerFactory: SequencerFactory, clock: Clock, orderMatchersRepository: OrderMatchersRepository) extends NewTradableHandler[NewOrderCancel] {

  import org.nexbook.sequence.SequencerFactory._

  val logger = LoggerFactory.getLogger(classOf[OrderCancelHandler])
  val tradeIDSequencer = sequencerFactory sequencer tradeIDSequencerName

  override def handle(newOrderCancel: NewOrderCancel) = {
    def acceptOrder(newOrderCancel: NewOrderCancel, prevOrder: Order): OrderCancel = new OrderCancel(tradeIDSequencer.nextValue, clock.currentDateTime, newOrderCancel.clOrdId, prevOrder)

    orderRepository.findBy(newOrderCancel.origClOrdId, newOrderCancel.connector) match {
      case Some(order) => orderMatchersRepository.find(newOrderCancel.symbol).processOrder(acceptOrder(newOrderCancel, order))
      case None => logger.warn("Unable to handle order cancel. Original order not found")
    }
  }
}
