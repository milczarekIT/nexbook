package org.nexbook.domain

sealed trait Side {
  def reverse: Side
}

case object Sell extends Side {
  def reverse = Buy
}

case object Buy extends Side {
  def reverse = Sell
}

object Side {
  def fromString(s: String):Side = s match {
    case "Buy" => Buy
    case "Sell" => Sell
    case _ => throw new IllegalArgumentException
  }
}


