package org.nexbook.repository

import org.nexbook.domain.{Order, OrderStatus}

/**
 * Created by milczu on 08.12.15.
 */
trait OrderRepository {

  def add(order: Order)

  def findAll: List[Order]

  def findBy(clOrdId: String, connector: String): Option[Order]

  def updateStatus(tradeID: Long, newStatus: OrderStatus, oldStatus: OrderStatus): Boolean
}
