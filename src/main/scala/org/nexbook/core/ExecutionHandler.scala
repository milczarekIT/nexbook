package org.nexbook.core

import org.nexbook.event.OrderProcessorEvent
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by milczu on 18.08.15.
 */
class ExecutionHandler extends mutable.Subscriber[OrderProcessorEvent, mutable.Publisher[OrderProcessorEvent]] {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  override def notify(pub: mutable.Publisher[OrderProcessorEvent], event: OrderProcessorEvent) = {
    LOGGER.info("Received event: " + event)
  }
}
