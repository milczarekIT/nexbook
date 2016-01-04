package org.nexbook.repository.mutable

import org.nexbook.domain.{Order, OrderStatus}

class OrderInMemoryRepository extends org.nexbook.repository.OrderInMemoryRepository {
  val orders = new scala.collection.mutable.HashMap[Long, Order]()

  override def add(order: Order) = orders(order.tradeID) = order

  override def findByClOrdId(clOrdId: String): Option[Order] = orders.values.find(_.clOrdId == clOrdId)

  override def findById(tradeID: Long): Option[Order] = orders.get(tradeID)

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

  override def findAll: List[Order] = orders.values.toList

  override def count: Int = orders.size
}
