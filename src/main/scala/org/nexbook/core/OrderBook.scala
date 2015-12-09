package org.nexbook.core

import org.nexbook.domain._
import org.nexbook.utils.OrderOrdering

import scala.collection.mutable

trait AbstractOrderBook {

  def add(order: LimitOrder)

  def remove(order: LimitOrder)

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
    book(side) removeTop()
  }

  def find(side: Side, tradeID: Long): Option[LimitOrder] = book(side) find tradeID

  override def remove(order: LimitOrder) = book(order.side) remove order

}

case class SideOrderBook(ordering: Ordering[LimitOrder]) extends AbstractOrderBook {

  val orders: mutable.SortedSet[LimitOrder] = mutable.TreeSet.empty(ordering)

  def add(order: LimitOrder) = orders += order

  def top: Option[LimitOrder] = orders.toSeq match {
    case Seq() => None
    case _ => Some(orders.head)
  }

  def removeTop() = if (orders.nonEmpty) orders -= orders.head

  def find(tradeID: Long) = orders.find(_.tradeID == tradeID)

  override def remove(order: LimitOrder): Unit = orders -= order
}

