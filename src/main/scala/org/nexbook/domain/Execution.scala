package org.nexbook.domain

import org.joda.time.DateTime

trait Execution extends Trade {
  val execID: Long
  val executionQty: Double
  val executionPrice: Double
}

case class OrderExecution(tradeID: Long, execID: Long, dealID: Long, executionQty: Double, executionPrice: Double, timestamp: DateTime, clOrdId: String, orderType: OrderType, side: Side, qty: Double, symbol: String, clientId: String, connector: String, override val leaveQty: Double) extends Execution {

  def this(tradeID: Long, execID: Long, order: Order, executionQty: Double, executionPrice: Double, executionTimestamp: DateTime) = this(tradeID, execID, order.dealID, executionQty, executionPrice, executionTimestamp, order.clOrdId, order.orderType, order.side, order.qty, order.symbol, order.clientId, order.connector, order.leaveQty)

  override val status: OrderStatus = if (leaveQty > 0) Partial else Filled

}

case class OrderRejection(tradeID: Long, execID: Long, dealID: Long, timestamp: DateTime, clOrdId: String, orderType: OrderType, side: Side, qty: Double, symbol: String, clientId: String, connector: String, override val leaveQty: Double, rejectReason: String) extends Execution {

  def this(tradeID: Long, execID: Long, order: Order, timestamp: DateTime, rejectReason: String) = this(tradeID, execID, order.dealID, timestamp, order.clOrdId, order.orderType, order.side, order.qty, order.symbol, order.clientId, order.connector, order.leaveQty, rejectReason)

  override val status: OrderStatus = Rejected
  override val executionPrice: Double = 0.00
  override val executionQty: Double = 0.00
}
