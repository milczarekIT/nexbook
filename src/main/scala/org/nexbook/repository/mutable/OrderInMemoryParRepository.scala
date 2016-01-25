package org.nexbook.repository.mutable

import org.nexbook.domain.{Order, OrderStatus}

import scala.collection.mutable

class OrderInMemoryParRepository extends org.nexbook.repository.OrderInMemoryRepository {

  val orders = new mutable.HashMap[Long, Order]().par

  override def add(order: Order) = synchronized { orders(order.tradeID) = order }

  override def findById(tradeID: Long): Option[Order] = orders.get(tradeID)

  override def findAll: List[Order] = orders.values.toList

  override def count: Int = orders.size

  override def findByClOrdId(clOrdId: String): Option[Order] = orders.find(_._2.clOrdId == clOrdId).map(_._2)

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
