package org.nexbook.orderprocessing.response

import org.nexbook.domain._

sealed trait OrderProcessingResponse {

  def payload: AnyRef
}

case class OrderAcceptResponse(order: Order) extends OrderProcessingResponse {
  override def payload: Order = order
}

case class OrderExecutionResponse(orderExecution: OrderExecution) extends OrderProcessingResponse {
  override def payload: OrderExecution = orderExecution
}

case class OrderRejectionResponse(rejection: OrderRejection) extends OrderProcessingResponse {
  override def payload: OrderRejection = rejection
}

case class OrderValidationRejectionResponse(validationRejection: OrderValidationRejection) extends OrderProcessingResponse {
  override def payload: OrderValidationRejection = validationRejection
}

