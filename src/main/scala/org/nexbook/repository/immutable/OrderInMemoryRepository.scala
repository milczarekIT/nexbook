package org.nexbook.repository.immutable

import org.nexbook.domain.{Order, OrderStatus}

/**
  * Created by milczu on 03.01.16.
  */
class OrderInMemoryRepository extends org.nexbook.repository.OrderInMemoryRepository {
  var orders = new scala.collection.immutable.HashMap[Long, Order]()

  override def add(order: Order): Unit = orders.synchronized {
	orders = orders + (order.tradeID -> order)
  }

  override def findByClOrdId(clOrdId: String): Option[Order] = orders.values.find(clOrdId == _.clOrdId)

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
