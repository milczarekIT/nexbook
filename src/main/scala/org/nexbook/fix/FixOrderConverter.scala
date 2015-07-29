package org.nexbook.fix

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain
import org.nexbook.domain._
import quickfix.field.{OrdType, Side, TransactTime}
import quickfix.fix44.NewOrderSingle

/**
 * Created by milczu on 16.12.14.
 */
object FixOrderConverter {

  def convert(fixOrder: NewOrderSingle): Order = {
    def resolveSide(fixSide: Side): domain.Side = fixSide.getValue match {
      case Side.BUY => Buy
      case Side.SELL => Sell
    }
    def toDateTime(transactTime: TransactTime): DateTime = new DateTime(transactTime.getValue.getTime, DateTimeZone.UTC)

    fixOrder.getOrdType.getValue match {
      case OrdType.LIMIT => new LimitOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue, fixOrder.getPrice.getValue)
      case OrdType.MARKET => new MarketOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue)
    }
  }

}
