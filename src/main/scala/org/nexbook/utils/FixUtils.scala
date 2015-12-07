package org.nexbook.utils

import org.nexbook.domain._
import quickfix.field.{OrdType, Side}

/**
 * Created by milczu on 07.12.15.
 */
object FixUtils {

  def ordType(order: Order): OrdType = order match {
    case l: LimitOrder => new OrdType(OrdType.LIMIT)
    case m: MarketOrder => new OrdType(OrdType.MARKET)
  }

  def side(order: Order): Side = order.side match {
    case Buy => new Side(Side.BUY)
    case Sell => new Side(Side.SELL)
  }

}
