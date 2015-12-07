package org.nexbook.repository

import org.nexbook.domain.Order

import scala.collection.mutable.TreeSet

class OrderRepository {
  val timestampDescOrdering = Ordering.fromLessThan[Order](_.timestamp isBefore _.timestamp)
  val orders = TreeSet.empty(timestampDescOrdering)

  def add(order: Order) = orders += order

  def findAll: List[Order] = orders.toList

}
