package org.nexbook.repository

import org.nexbook.domain.Order
import org.nexbook.utils.OrderOrdering

import scala.collection.mutable

class OrderInMemoryRepository extends OrderRepository {
  val orders = mutable.TreeSet.empty(OrderOrdering.orderTradeIDDesc)

  def add(order: Order) = orders += order

  def findAll: List[Order] = orders.toList

}
