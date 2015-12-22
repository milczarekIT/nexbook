package org.nexbook.core

import org.nexbook.domain._
import org.nexbook.utils.{BookOrdering, BuyBookOrdering, SellBookOrdering}

import scala.collection.mutable

trait AbstractOrderBook {

  def add(order: LimitOrder)

  def remove(order: LimitOrder)

}

class OrderBook extends AbstractOrderBook {

  val buyBook = new SideOrderBook(BuyBookOrdering)
  val sellBook = new SideOrderBook(SellBookOrdering)

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

  def priceLevels(side: Side): List[(Double, Double)] = book(side) priceLevels

  def depth(side: Side) = book(side) depth

  def size(side: Side) = book(side) size

}

class SideOrderBook(ordering: BookOrdering) extends AbstractOrderBook {

  val orders: mutable.SortedSet[LimitOrder] = mutable.TreeSet.empty(ordering.orderOrdering)

  def add(order: LimitOrder) = orders += order

  def top: Option[LimitOrder] = orders.toSeq match {
	case Seq() => None
	case _ => Some(orders.head)
  }

  def removeTop() = if (orders.nonEmpty) orders -= orders.head

  def find(tradeID: Long) = orders.find(_.tradeID == tradeID)

  override def remove(order: LimitOrder): Unit = orders -= order

  def priceLevels: List[(Double, Double)] = {
	orders.groupBy(_.limit).map(e => (e._1, e._2.foldLeft(0.00)(_ + _.leaveQty.toInt))).toList.sorted(Ordering.Tuple2(ordering.priceOrdering, ordering.priceOrdering))
  }

  def depth: Int = orders.groupBy(_.limit).keySet.size

  def size = orders.size
}

