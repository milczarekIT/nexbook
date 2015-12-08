package org.nexbook.orderprocessing.response

import org.nexbook.domain._

sealed trait OrderProcessingResponse {

  def payload: ProcessingResponse
}

case class OrderRejectionResponse(rejection: OrderRejection) extends OrderProcessingResponse {
  override def payload: OrderRejection = rejection
}

case class OrderValidationRejectionResponse(order: NewOrder, rejectReason: String) extends OrderProcessingResponse {
  override def payload: ProcessingResponse = ???
}

case class OrderExecutionResponse(dealDone: DealDone) extends OrderProcessingResponse {
  override def payload: DealDone = dealDone
}