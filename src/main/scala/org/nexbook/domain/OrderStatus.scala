package org.nexbook.domain

/**
  * Created by milczu on 09.12.15
  */
sealed trait OrderStatus

case object New extends OrderStatus

case object Working extends OrderStatus

case object Partial extends OrderStatus

case object Filled extends OrderStatus

case object Rejected extends OrderStatus

case object Cancelled extends OrderStatus

object OrderStatus {

  val orderFinishedStatuses = List(Filled, Rejected, Cancelled)

  def fromString(s: String): OrderStatus = s match {
	case "New" => New
	case "Working" => Working
	case "Partial" => Partial
	case "Filled" => Filled
	case "Rejected" => Rejected
	case "Cancelled" => Cancelled
	case _ => throw new IllegalArgumentException
  }
}