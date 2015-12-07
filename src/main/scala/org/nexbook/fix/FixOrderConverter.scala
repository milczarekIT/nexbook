package org.nexbook.fix

import org.nexbook.domain
import org.nexbook.domain._
import quickfix.field.{OrdType, SenderCompID, Side}
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
    def senderCompId: String = fixOrder.getHeader.getField(new SenderCompID()).getValue

    fixOrder.getOrdType.getValue match {
      case OrdType.LIMIT => new LimitOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue, fixOrder.getPrice.getValue, senderCompId)
      case OrdType.MARKET => new MarketOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue, senderCompId)
    }
  }

}
