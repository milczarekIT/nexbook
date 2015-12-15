package org.nexbook.domain

import org.joda.time.DateTime

trait Execution extends Trade {
  val execID: Long
  val executionQty: Double
  val executionPrice: Double
  val executionSpeed: Int
}

case class OrderExecution(tradeID: Long, execID: Long, dealID: Long, executionSpeed: Int, qty: Double, executionQty: Double, executionPrice: Double, override val leaveQty: Double, side: Side, symbol: String, timestamp: DateTime, clOrdId: String, orderType: OrderType, clientId: String, connector: String) extends Execution {

  def this(tradeID: Long, execID: Long, order: Order, executionQty: Double, executionPrice: Double, executionTimestamp: DateTime) = this(tradeID, execID, order.dealID, (executionTimestamp.getMillis - order.timestamp.getMillis).toInt, order.qty, executionQty, executionPrice, order.leaveQty, order.side, order.symbol, executionTimestamp, order.clOrdId, order.orderType, order.clientId, order.connector)

  override val status: OrderStatus = if (leaveQty > 0) Partial else Filled

}

case class OrderRejection(tradeID: Long, execID: Long, dealID: Long, executionSpeed: Int, qty: Double, override val leaveQty: Double, side: Side, symbol: String, timestamp: DateTime, clOrdId: String, orderType: OrderType, clientId: String, connector: String, rejectReason: String) extends Execution {

  def this(tradeID: Long, execID: Long, order: Order, timestamp: DateTime, rejectReason: String) = this(tradeID, execID, order.dealID, (timestamp.getMillis - order.timestamp.getMillis).toInt, order.qty, order.leaveQty, order.side, order.symbol, timestamp, order.clOrdId, order.orderType, order.clientId, order.connector, rejectReason)

  override val status: OrderStatus = Rejected
  override val executionPrice: Double = 0.00
  override val executionQty: Double = 0.00
}
