package org.nexbook.domain

/**
 * Created by milczu on 08.12.15.
 */
trait TradeDetails {

  val symbol: String
  val clientId: String
  val qty: Double
  val side: Side
  val orderType: OrderType
  val connector: String
  val clOrdId: String

  def status: OrderStatus
}
