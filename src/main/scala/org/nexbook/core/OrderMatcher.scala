package org.nexbook.core

import org.joda.time.DateTime
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.response.{OrderAcceptResponse, OrderBookResponse, OrderExecutionResponse, OrderRejectionResponse}
import org.nexbook.repository.OrderInMemoryRepository
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.Clock
import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode

class OrderMatcher(orderRepository: OrderInMemoryRepository, sequencerFactory: SequencerFactory, book: OrderBook, orderBookResponseHandlers: List[Handler[OrderBookResponse]], clock: Clock) {

  val logger = LoggerFactory.getLogger(classOf[OrderMatcher])
  val bookLogger = LoggerFactory.getLogger("BOOK_LOG")

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
		  case Some(unfilled) =>
			book add unfilled
			if (bookLogger.isDebugEnabled) {
			bookLogger.debug(s"2) Book (${order.side}:${order.symbol}) size: ${book.size(order.side)}, depth: ${book.depth(order.side)}, price levels: ${book.priceLevels(order.side)}")
			}
		  case _ => logger.trace(s"Order ad-hoc filled or rejected: $order")
		}
	}
  }

  protected def tryMatch(order: Order, firstCounterOrder: Option[LimitOrder]): Option[LimitOrder] = firstCounterOrder match {
	case None =>
	  order match {
		case o: MarketOrder =>
		  logger.debug(s"Rejection for order: $o with remaining size: ${o.leaveQty}")
		  orderBookResponseHandlers.foreach(_.handle(OrderRejectionResponse(new OrderRejection(tradeIDSequencer.nextValue, execIDSequencer.nextValue, o, clock.currentDateTime, "No orders in book"))))
		  None
		case o: LimitOrder => Some(o)
	  }
	case Some(counterOrder) =>
	  if (ordersCrossing(order, counterOrder)) {
		val dealDone = matchOrders(order, counterOrder)
		logger.debug("Deal done: " + dealDone)
		dealDoneToExecutions(dealDone).foreach(execution => orderBookResponseHandlers.foreach(_.handle(OrderExecutionResponse(execution))))

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
	val execDateTime = clock.currentDateTime
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

	if(bookLogger.isDebugEnabled) {
	  bookLogger.debug(s"1) Book (${order.side}:${order.symbol}) size: ${book.size(order.side)}, depth: ${book.depth(order.side)}, price levels: ${book.priceLevels(order.side)}")
	}

	DealDone(execIDSequencer.nextValue, buyOrder, sellOrder, dealSize, dealPrice, execDateTime)
  }

  def tryCancel(orderCancel: OrderCancel): Unit = {
	logger.info(s"Handled order cancel: $orderCancel")
	book find(orderCancel.side, orderCancel.dealID) match {
	  case Some(order) =>
		if (OrderStatus.orderFinishedStatuses.contains(order.status)) {
		  logger.warn(s"Unable to cancel order by ${orderCancel.tradeID}. Order ${orderCancel.dealID} already finished")
		} else {
		  book remove order
		  orderRepository.updateStatus(order.tradeID, Cancelled, order.status)
		  orderBookResponseHandlers.foreach(_.handle(OrderAcceptResponse(orderCancel)))
		}
	  case None =>
		orderRepository.findById(orderCancel.dealID) match {
		  case Some(o) => logger.debug(s"Unable to cancel order: ${orderCancel.dealID}. Order to cancel not found. Cancelling order: ${orderCancel.tradeID}. Orig Order status ${o.status}")
		  case None => logger.debug(s"Unable to cancel order: ${orderCancel.dealID}. Order to cancel not found. Cancelling order: ${orderCancel.tradeID}")
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

