package org.nexbook.utils

import org.nexbook.domain._
import quickfix.field.OrdType

/**
  * Created by milczu on 07.12.15.
  */
object FixUtils {

  def ordType(orderType: OrderType): OrdType = orderType match {
	case Limit => new OrdType(OrdType.LIMIT)
	case Market => new OrdType(OrdType.MARKET)
  }

  def side(side: Side): quickfix.field.Side = side match {
	case Buy => new quickfix.field.Side(quickfix.field.Side.BUY)
	case Sell => new quickfix.field.Side(quickfix.field.Side.SELL)
  }

}
