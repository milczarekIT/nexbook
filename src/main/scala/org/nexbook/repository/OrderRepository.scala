package org.nexbook.repository

import org.nexbook.domain.Order
import org.nexbook.utils.OrderOrdering

import scala.collection.mutable

class OrderRepository {
  val timestampDescOrdering = OrderOrdering.timestampDesc
  val orders = mutable.TreeSet.empty(timestampDescOrdering)

  def add(order: Order) = orders += order

  def findAll: List[Order] = orders.toList

}
