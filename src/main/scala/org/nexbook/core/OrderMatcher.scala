package org.nexbook.core

import org.joda.time.DateTime
import org.nexbook.domain._
import org.nexbook.orderprocessing.OrderProcessingResponseSender
import org.nexbook.orderprocessing.response.{OrderAcceptResponse, OrderExecutionResponse, OrderRejectionResponse}
import org.nexbook.repository.OrderInMemoryRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock
import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode

class OrderMatcher(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, book: OrderBook, orderProcessingSender: OrderProcessingResponseSender, clock: Clock) {

  val logger = LoggerFactory.getLogger(classOf[OrderMatcher])

  import org.nexbook.sequence.SequencerFactory._

  val tradeIDSequencer = sequencerFactory sequencer tradeIDSequencerName
  val execIDSequencer = sequencerFactory sequencer execIDSequencerName

  def processOrder(order: Order) = this.synchronized {
    orderRepository add order
    order match {
      case cancel: OrderCancel => tryCancel(cancel)
      case _ =>
        val firstCounterOrder = book top order.side.reverse
        val unfilledOrder = tryMatch(order, firstCounterOrder)
        unfilledOrder match {
          case Some(unfilled) => book add unfilled
          case _ => logger.trace("Order ad-hoc filled or rejected: {}", order)
        }
    }
  }

  protected def tryMatch(order: Order, firstCounterOrder: Option[LimitOrder]): Option[LimitOrder] = firstCounterOrder match {
    case None =>
      order match {
        case o: MarketOrder =>
          logger.debug("Rejection for order: {} with remaining size: {}", o, o.leaveQty)
          orderProcessingSender.send(OrderRejectionResponse(new OrderRejection(tradeIDSequencer.nextValue, execIDSequencer.nextValue, o, clock.getCurrentDateTime, "No orders in book")))
          None
        case o: LimitOrder => Some(o)
      }
    case Some(counterOrder) =>
      if (ordersCrossing(order, counterOrder)) {
        val dealDone = matchOrders(order, counterOrder)
        logger.debug("Deal done: " + dealDone)
        dealDoneToExecutions(dealDone).foreach(execution => orderProcessingSender.send(OrderExecutionResponse(execution)))

        if (order.leaveQty > 0) tryMatch(order, book top order.side.reverse)
        else None
      } else {
        Some(order.asInstanceOf[LimitOrder])
      }
  }

  private def ordersCrossing(order: Order, counter: LimitOrder): Boolean = order match {
    case o: MarketOrder => true
    case o: LimitOrder => o.side match {
      case Buy => o.limit >= counter.limit
      case Sell => o.limit <= counter.limit
    }
  }


  private def matchOrders(order: Order, counterOrder: LimitOrder): DealDone = {
    val execDateTime = clock.getCurrentDateTime
    def determineDealSize(order: Order, counter: LimitOrder): Double = if (order.leaveQty <= counter.leaveQty) order.leaveQty else counter.leaveQty
    def determineDealPrice(order: Order, counter: LimitOrder): Double = order match {
      case o: MarketOrder => counter.limit
      case o: LimitOrder => if (o.limit == counter.limit) o.limit else BigDecimal((o.limit + counter.limit) / 2.0).setScale(5, RoundingMode.HALF_DOWN).toDouble
    }

    val dealSize = determineDealSize(order, counterOrder)
    val dealPrice = determineDealPrice(order, counterOrder)
    order addFillQty dealSize
    counterOrder addFillQty dealSize
    if (counterOrder.leaveQty == 0.00) {
      book removeTop counterOrder.side
    }
    val buyOrder = if (order.side == Buy) order else counterOrder
    val sellOrder = if (order.side == Buy) counterOrder else order

    DealDone(execIDSequencer.nextValue, buyOrder, sellOrder, dealSize, dealPrice, execDateTime)
  }

  def tryCancel(orderCancel: OrderCancel): Unit = {
    logger.info("Handled order cancel: {}", orderCancel)
    book find(orderCancel.side, orderCancel.dealID) match {
      case Some(order) =>
        if (OrderStatus.orderFinishedStatuses.contains(order.status)) {
          logger.warn("Unable to cancel order by {}. Order {} already finished", orderCancel.tradeID, orderCancel.dealID)
        } else {
          book remove order
          orderRepository.updateStatus(order.tradeID, Cancelled, order.status)
          orderProcessingSender.send(OrderAcceptResponse(orderCancel))
        }
      case None =>
        orderRepository.findById(orderCancel.dealID) match {
          case Some(o) => logger.debug("Unable to cancel order: {}. Order to cancel not found. Cancelling order: {}. Orig Order status " + o.status.toString, orderCancel.dealID, orderCancel.tradeID)
          case None => logger.debug("Unable to cancel order: {}. Order to cancel not found. Cancelling order: {}", orderCancel.dealID, orderCancel.tradeID)
        }

    }
  }

  def dealDoneToExecutions(dealDone: DealDone): List[OrderExecution] = {
    val buyExecution = new OrderExecution(tradeIDSequencer.nextValue, dealDone.execID, dealDone.buy, dealDone.dealSize, dealDone.dealPrice, dealDone.executionTime)
    val sellExecution = new OrderExecution(tradeIDSequencer.nextValue, dealDone.execID, dealDone.sell, dealDone.dealSize, dealDone.dealPrice, dealDone.executionTime)
    List(buyExecution, sellExecution)
  }

  case class DealDone(execID: Long, buy: Order, sell: Order, dealSize: Double, dealPrice: Double, executionTime: DateTime)

}

