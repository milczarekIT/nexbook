package org.nexbook.repository.mutable

import org.nexbook.domain.Order

class OrderInMemoryParRepository extends OrderInMemoryRepository {

  override def add(order: Order) = synchronized { orders(order.clOrdId) = order }

  override def findById(tradeID: Long): Option[Order] = orders.values.par.find(_.tradeID == tradeID)

  override def count: Int = orders.size

  override def findByClOrdId(clOrdId: String): Option[Order] = (orders.valuesIterator).find(_.clOrdId == clOrdId)

}
