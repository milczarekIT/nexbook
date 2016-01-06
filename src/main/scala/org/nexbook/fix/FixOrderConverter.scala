package org.nexbook.fix

import org.nexbook.domain
import org.nexbook.domain._
import org.nexbook.utils.Clock
import quickfix.field.{OrdType, SenderCompID, Side}
import quickfix.fix44.{Message, NewOrderSingle, OrderCancelRequest}

/**
  * Created by milczu on 16.12.14.
  */
class FixOrderConverter(clock: Clock) {

  def convert(fixOrder: NewOrderSingle): NewOrder = fixOrder.getOrdType.getValue match {
	case OrdType.LIMIT => new NewLimitOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue, fixOrder.getPrice.getValue, senderCompId(fixOrder), clock.currentDateTime)
	case OrdType.MARKET => new NewMarketOrder(fixOrder.getClOrdID.getValue, fixOrder.getSymbol.getValue, fixOrder.getAccount.getValue, resolveSide(fixOrder.getSide), fixOrder.getOrderQty.getValue, senderCompId(fixOrder), clock.currentDateTime)
  }

  private def resolveSide(fixSide: Side): domain.Side = fixSide.getValue match {
	case Side.BUY => Buy
	case Side.SELL => Sell
  }

  def senderCompId(msg: Message): String = msg.getHeader.getField(new SenderCompID()).getValue

  def convert(orderCancelRequest: OrderCancelRequest): NewOrderCancel = NewOrderCancel(orderCancelRequest.getClOrdID.getValue, orderCancelRequest.getOrigClOrdID.getValue, senderCompId(orderCancelRequest), orderCancelRequest.getSymbol.getValue, resolveSide(orderCancelRequest.getSide))

}
