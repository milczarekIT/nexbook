package org.nexbook.domain

/**
 * Created by milczu on 08.12.15.
 */
sealed trait NewOrder extends TradeDetails with NewTradable

case class NewLimitOrder(clOrdId: String, symbol: String, clientId: String, side: Side, qty: Double, limit: Double, connector: String, orderType: OrderType = Limit, status: OrderStatus = New) extends NewOrder

case class NewMarketOrder(clOrdId: String, symbol: String, clientId: String, side: Side, qty: Double, connector: String, orderType: OrderType = Market, status: OrderStatus = New) extends NewOrder
