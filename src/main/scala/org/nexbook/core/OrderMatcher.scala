package org.nexbook.core

import org.nexbook.domain._
import org.nexbook.orderprocessing.OrderProcessingResponseSender
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderRejectionResponse}
import org.nexbook.utils.Clock
import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode

class OrderMatcher(execIDSequencer: Sequencer, book: OrderBook, orderSender: OrderProcessingResponseSender, clock: Clock) {

  val logger = LoggerFactory.getLogger(classOf[OrderMatcher])

  def acceptOrder(order: Order) = this.synchronized {
    val firstCounterOrder = book top order.side.reverse
    val unfilledOrder = tryMatch(order, firstCounterOrder)
    unfilledOrder match {
      case Some(unfilled) => book add unfilled
      case _ => logger.info("Order adhoc filled: {}", order)
    }

  }

  protected def tryMatch(order: Order, firstCounterOrder: Option[LimitOrder]): Option[LimitOrder] = firstCounterOrder match {
    case None =>
      order match {
        case o: MarketOrder =>
          orderSender.send(OrderRejectionResponse(OrderRejection(execIDSequencer.nextValue, o, "No orders in book", clock.getCurrentDateTime)))
          None
        case o: LimitOrder => Some(o)
      }
    case Some(counterOrder) =>
      if (ordersCrossing(order, counterOrder)) {
        val dealDone = matchOrders(order, counterOrder)
        logger.debug("Deal done: " + dealDone)
        orderSender.send(OrderExecutionResponse(dealDone))

        if (order.remainingSize > 0) tryMatch(order, book top order.side.reverse)
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
    def determineDealSize(order: Order, counter: LimitOrder): Double = if (order.remainingSize <= counter.remainingSize) order.remainingSize else counter.remainingSize
    def determineDealPrice(order: Order, counter: LimitOrder): Double = order match {
      case o: MarketOrder => counter.limit
      case o: LimitOrder => if (o.limit == counter.limit) o.limit else BigDecimal((o.limit + counter.limit) / 2.0).setScale(5, RoundingMode.HALF_DOWN).toDouble
    }

    val dealSize = determineDealSize(order, counterOrder)
    val dealPrice = determineDealPrice(order, counterOrder)
    order addFillSize dealSize
    counterOrder addFillSize dealSize
    if (counterOrder.remainingSize == 0.00) {
      book removeTop counterOrder.side
    }
    val buyOrder = if (order.side == Buy) order else counterOrder
    val sellOrder = if (order.side == Buy) counterOrder else order

    DealDone(execIDSequencer.nextValue, buyOrder, sellOrder, dealSize, dealPrice, execDateTime)
  }

}

