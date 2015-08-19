package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain._
import org.nexbook.event.{OrderExecutionEvent, OrderProcessorEvent, OrderRejectionEvent}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.math.BigDecimal.RoundingMode

class OrderMatcher(book: OrderBook) extends mutable.Publisher[OrderProcessorEvent] {

  val LOGGER = LoggerFactory.getLogger(classOf[OrderMatcher])

  def acceptOrder(order: Order) = this.synchronized {
    val firstCounterOrder = book top order.side.reverse
    val unfilledOrder = tryMatch(order, firstCounterOrder)
    unfilledOrder match {
      case Some(unfilledOrder) => book add unfilledOrder
      case _ => LOGGER.trace("Order adhoc filled: {}", order)
    }

  }

  protected def tryMatch(order: Order, firstCounterOrder: Option[LimitOrder]): Option[LimitOrder] = firstCounterOrder match {
    case None => {
      order match {
        case o: MarketOrder => {
          publish(OrderRejectionEvent(order,"TODO "))
          None
        }
        case o: LimitOrder => Some(o)
      }
    }
    case Some(counterOrder) => {
      if (ordersCrossing(order, counterOrder)) {
        val dealDone = matchOrders(order, counterOrder)
        println("Deal done: " + dealDone)
        publish(OrderExecutionEvent(dealDone))


        if (order.remainingSize > 0) tryMatch(order, book top order.side.reverse)
        else None
      } else {
        Some(order.asInstanceOf[LimitOrder])
      }
    }
  }

  private def ordersCrossing(order: Order, counter: LimitOrder): Boolean = order match {
    case o: MarketOrder => true
    case o: LimitOrder => o.side match {
      case Buy => o.limit >= counter.limit
      case Sell => o.limit <= counter.limit
    }
  }

  /**
   * @return (buyOrder, sellOrder, dealSize, dealPrice)
   */
  private def matchOrders(order: Order, counterOrder: LimitOrder): DealDone = {
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

    DealDone(buyOrder, sellOrder, dealSize, dealPrice, DateTime.now(DateTimeZone.UTC))
  }

}

