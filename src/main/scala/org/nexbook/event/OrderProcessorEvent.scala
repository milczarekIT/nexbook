package org.nexbook.event

import org.nexbook.domain.{DealDone, Order}

sealed trait OrderProcessorEvent

case class OrderRejectionEvent(order: Order) extends OrderProcessorEvent

case class OrderExecutionEvent(dealDone: DealDone) extends OrderProcessorEvent