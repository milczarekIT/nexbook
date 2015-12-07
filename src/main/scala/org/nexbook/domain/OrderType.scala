package org.nexbook.domain

sealed trait OrderType

case object Limit extends OrderType

case object Market extends OrderType

object OrderType {

  def fromString(s: String): OrderType = s match {
    case "Limit" => Limit
    case "Market" => Market
    case _ => throw new IllegalArgumentException
  }
}
