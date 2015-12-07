package org.nexbook.orderprocessing.response

import org.nexbook.domain.{DealDone, Order, OrderRejection}

sealed trait OrderProcessingResponse

case class OrderRejectionResponse(rejection: OrderRejection) extends OrderProcessingResponse

case class OrderValidationRejectionResponse(order: Order, rejectReason: String) extends OrderProcessingResponse

case class OrderExecutionResponse(dealDone: DealDone) extends OrderProcessingResponse