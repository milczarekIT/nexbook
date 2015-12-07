package org.nexbook.domain

import org.joda.time.DateTime

class OrderExecution(order: Order, executionSequence: Long, executionSize: Double, executionPrice: Double, executionTimestamp: DateTime) extends Order {

  val tradeID = order.tradeID
  val orderType = order.orderType
  val side = order.side
  val size = order.size
  val symbol = order.symbol
  setTimestamp(executionTimestamp)
  val clientId = order.clientId
  val fixId = order.fixId

}
