package org.nexbook.core

import org.nexbook.domain._

import scala.collection.mutable.{SortedSet, TreeSet}


trait AbstractOrderBook {

  def add(order: LimitOrder)

}

class OrderBook extends AbstractOrderBook {

  val buyBook = SideOrderBook(OrderOrdering.bookBuyOrdering)
  val sellBook = SideOrderBook(OrderOrdering.bookSellOrdering)

  def add(order: LimitOrder) = {
    book(order.side) add order
  }

  def book(side: Side): SideOrderBook = side match {
    case Buy => buyBook
    case Sell => sellBook
  }

  def top(side: Side): Option[LimitOrder] = {
    book(side) top
  }

  def removeTop(side: Side) {
    book(side) removeTop
  }

}

case class SideOrderBook(ordering: Ordering[LimitOrder]) extends AbstractOrderBook {

  val orders: SortedSet[LimitOrder] = TreeSet.empty(ordering)

  def add(order: LimitOrder) = orders += order

  def top: Option[LimitOrder] = orders.toSeq match {
    case Seq() => None
    case _ => Some(orders.head)
  }

  def removeTop = if (!orders.isEmpty) orders -= orders.head
}

