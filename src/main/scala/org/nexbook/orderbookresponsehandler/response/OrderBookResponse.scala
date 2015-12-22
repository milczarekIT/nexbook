package org.nexbook.orderbookresponsehandler.response

import org.nexbook.domain._

sealed trait OrderBookResponse {

  def payload: AnyRef
}

case class OrderAcceptResponse(order: Order) extends OrderBookResponse {
  override def payload: Order = order
}

case class OrderExecutionResponse(orderExecution: OrderExecution) extends OrderBookResponse {
  override def payload: OrderExecution = orderExecution
}

case class OrderRejectionResponse(rejection: OrderRejection) extends OrderBookResponse {
  override def payload: OrderRejection = rejection
}

case class OrderValidationRejectionResponse(validationRejection: OrderValidationRejection) extends OrderBookResponse {
  override def payload: OrderValidationRejection = validationRejection
}

