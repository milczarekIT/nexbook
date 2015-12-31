package org.nexbook.orderchange

import org.nexbook.core.Command

/**
  * Created by milczu on 12/23/15.
  */
case class OrderChangeCommand(orderChange: OrderChange) extends Command[OrderChange] {
  override def payload: OrderChange = orderChange
}
