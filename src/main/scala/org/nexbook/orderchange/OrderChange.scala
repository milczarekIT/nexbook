package org.nexbook.orderchange

import org.apache.commons.lang3.builder.{ToStringBuilder, ToStringStyle}
import org.nexbook.domain.OrderStatus


/**
  * Created by milczu on 12/23/15.
  */
sealed trait OrderChange {
  val tradeID: Long
}

class OrderStatusChange(val tradeID: Long, val prevStatus: OrderStatus, val status: OrderStatus) extends OrderChange {
  override def toString: String = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("tradeID", tradeID).append("prevStatus", prevStatus).append("status", status).build()
}

class OrderFillChange(val tradeID: Long, val prevStatus: OrderStatus, val status: OrderStatus, val prevLeaveQty: Double, val leaveQty: Double) extends OrderChange {
  override def toString: String = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("tradeID", tradeID).append("prevStatus", prevStatus).append("status", status).append("prevLeaveQty", prevLeaveQty).append("leaveQty", leaveQty).build()
}
