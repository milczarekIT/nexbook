package org.nexbook.repository

import org.nexbook.domain.{Order, OrderStatus}
import org.nexbook.utils.OrderOrdering

import scala.collection.mutable

class OrderInMemoryRepository extends OrderRepository {
  val orders = mutable.TreeSet.empty(OrderOrdering.orderTradeIDDesc)

  def add(order: Order) = orders += order

  def findAll: List[Order] = orders.toList

  override def findBy(clOrdId: String, connector: String): Option[Order] = orders.find(o => o.clOrdId == clOrdId && o.connector == connector)

  def findById(tradeID: Long): Option[Order] = orders.find(_.tradeID == tradeID)

  override def updateStatus(tradeID: Long, newStatus: OrderStatus, oldStatus: OrderStatus): Boolean = {
	findById(tradeID) match {
	  case Some(order) =>
		if (order.status == oldStatus) {
		  order.updateStatus(newStatus)
		  true
		}
		else false
	  case None => false
	}
  }
}
