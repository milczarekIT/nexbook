package org.nexbook.domain

import org.joda.time.DateTime

class OrderExecution(order: Order, executionSequence: Long, executionSize: Double, executionPrice: Double, executionTimestamp: DateTime) extends Order {

  val clOrdId = order.clOrdId
  val orderType = order.orderType
  val side = order.side
  val qty = order.qty
  val symbol = order.symbol
  val clientId = order.clientId
  val connector = order.connector
  val timestamp = executionTimestamp
  val tradeID = executionSequence

}
