package org.nexbook.testutils

import java.util.concurrent.atomic.AtomicLong

import org.nexbook.domain._
import org.nexbook.fix.FixOrderConverter
import org.nexbook.utils.DefaultClock
import quickfix.Message
import quickfix.field.MsgType
import quickfix.fix44.{NewOrderSingle, OrderCancelRequest}


/**
  * Created by milczu on 03.01.16.
  */
object OrderProvider {

  type converter = Message => Order
  val testDataDir = "src/test/resources/data"
  val defaultTestDataFile = "orders8_200k.txt"
  val clock = new DefaultClock
  val fixConverter = new FixOrderConverter(clock)
  val tradeIDGenerator = new AtomicLong

  def get(fileName: String): List[Order] = get(fileName, None)

  def get(): List[Order] = get(None)

  def get(limit: Option[Int]): List[Order] = get(defaultTestDataFile, limit)

  def get(fileName: String, limit: Option[Int]): List[Order] = {
	val fixMsgs = FixMessageProvider.get(s"$testDataDir/$fileName", limit).map(_._1)
	val fixNewOrderSingles = fixMsgs.filter(m => NewOrderSingle.MSGTYPE == msgType(m))

	val newOrders: List[Order] = fixNewOrderSingles.map(asNewTradable _ andThen asOrder)

	val fixOrderCancelRequests = fixMsgs.filter(m => OrderCancelRequest.MSGTYPE == msgType(m))
	val newOrdersByClOrdId: Map[String, Order] = newOrders.map(o => o.clOrdId -> o).toMap

	val orderCancels = fixOrderCancelRequests.map(asNewOrderCancel).map(c => asOrderCancel(c, newOrdersByClOrdId(c.origClOrdId)))

	newOrders ::: orderCancels
  }

  def asNewTradable(m: Message): NewTradable = msgType(m) match {
	case NewOrderSingle.MSGTYPE => fixConverter.convert(m.asInstanceOf[NewOrderSingle])
	case _ => throw new IllegalArgumentException
  }

  def msgType(m: Message): String = m.getHeader.getField(new MsgType()).getValue

  def asOrder(t: NewTradable): Order = t match {
	case l: NewLimitOrder => new LimitOrder(l, tradeIDGenerator.incrementAndGet)
	case m: NewMarketOrder => new MarketOrder(m, tradeIDGenerator.incrementAndGet)
  }

  def asNewOrderCancel(m: Message): NewOrderCancel = fixConverter.convert(m.asInstanceOf[OrderCancelRequest])

  def asOrderCancel(c: NewOrderCancel, origOrder: Order): OrderCancel = {
	new OrderCancel(tradeIDGenerator.incrementAndGet, clock.currentDateTime, c.clOrdId, origOrder)
  }

  def get(limit: Int): List[Order] = get(Some(limit))


}
