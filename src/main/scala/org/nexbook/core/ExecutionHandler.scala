package org.nexbook.core

import org.nexbook.event.{OrderRejectionEvent, OrderExecutionEvent, OrderProcessorEvent}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 18.08.15.
 */
class ExecutionHandler extends mutable.Subscriber[OrderProcessorEvent, mutable.Publisher[OrderProcessorEvent]] {

  val LOGGER = LoggerFactory.getLogger(classOf[ExecutionHandler])

  override def notify(pub: mutable.Publisher[OrderProcessorEvent], event: OrderProcessorEvent) = event match {
    case execution: OrderExecutionEvent => LOGGER.info("Handled deal done: {}", execution.dealDone)
    case rejection: OrderRejectionEvent => LOGGER.info("Rejection reject reason: {}", rejection.rejectReason)
  }
}
