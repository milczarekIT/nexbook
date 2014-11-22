package org.nexbook.domain

import org.joda.time.DateTime

class OrderExecution(order: Order, executionSequence: Long, executionTimestamp: DateTime, executionSize: Double, executionPrice: Double) extends Order {

  val sequence = executionSequence
  val orderType = order.orderType
  val side = order.side
  val size = order.size
  val symbol = order.symbol
  val timestamp = executionTimestamp
  val clientId = order.clientId


}
