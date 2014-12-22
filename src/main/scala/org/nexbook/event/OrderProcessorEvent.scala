package org.nexbook.event

import org.joda.time.DateTime
import org.nexbook.domain.Order

sealed trait OrderProcessorEvent

case class OrderRejectionEvent(order: Order) extends OrderProcessorEvent

case class OrderExecutionEvent(buy: Order, sell: Order, dealSize: Double, executionTime: DateTime) extends OrderProcessorEvent