package org.nexbook.core

import org.joda.time.{DateTimeZone, DateTime}
import org.mockito.ArgumentMatcher
import org.nexbook.domain._
import org.nexbook.sequence.Sequencer
import org.nexbook.orderprocessing.OrderProcessingResponseSender
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderRejectionResponse, OrderProcessingResponse}
import org.nexbook.utils.DefaultClock
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar._
import org.mockito.Mockito.{verify, spy, never, times}
import org.mockito.Matchers._


/**
 * Created by milczu on 08.12.15.
 */
class OrderMatcherTest extends FlatSpec with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  def limitOrder(side: Side = Buy, limit: Double = 4.32, size: Double  = 100, sequence: Long = 1) = LimitOrder(sequence, "EUR/PLN", "cl1", side, size, limit, "FIX1", now, "1", Limit)
  def marketOrder(side: Side = Buy, size: Double = 100, sequence: Long = 2) = MarketOrder(sequence, "EUR/PLN", "cl2", side, size, "FIX2", now, "2", Market)

  "Empty OrderMatcher" should "send rejection for first MarketOrder" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val marketOrder1 = marketOrder()

    orderMatcher.processOrder(marketOrder1)

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)

    verify(orderSender).send(any(classOf[OrderProcessingResponse]))
    verify(orderBook, never).add(any(classOf[LimitOrder]))
  }

  "Empty OrderMatcher" should "should add original LimitOrder to OrderBook" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val order = limitOrder()

    orderMatcher.processOrder(order)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual order
    orderBook.top(Sell) should be (None)

    verify(orderSender, never).send(any(classOf[OrderRejectionResponse]))
    verify(orderBook, times(1)).add(any(classOf[LimitOrder]))
  }

  "OrderMatcher" should "generate one deal: 2 orders on both sides with same size and price" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = price, size = size)
    val orderSell = limitOrder(side = Sell, limit = price, size = size)

    orderBuy.qty shouldEqual orderSell.qty
    orderBuy.limit shouldEqual orderSell.limit

    orderMatcher.processOrder(orderBuy)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual orderBuy
    orderBook.top(Sell) should be (None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy)  should be (None)
    orderBook.top(Sell) should be (None)

    verify(orderSender).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
  }

  "OrderMatcher" should "generate one deal: 2 orders on both sides with same size. First: Limit, Second: Market" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = price, size = size)
    val orderSell = marketOrder(side = Sell, size = size)

    orderBuy.qty shouldEqual orderSell.qty

    orderMatcher.processOrder(orderBuy)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual orderBuy
    orderBook.top(Sell) should be (None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy)  should be (None)
    orderBook.top(Sell) should be (None)

    verify(orderSender).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
  }

  "OrderMatcher" should "generate 2 deals: 2 buy orders, 1 sell order, with same size. Size is matching, Fulfill" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy1 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 1)
    val orderBuy2 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 2)
    val orderSell = marketOrder(side = Sell, size = size, sequence = 3)

    orderBuy1.qty shouldEqual orderSell.qty / 2

    orderMatcher.processOrder(orderBuy1)
    orderMatcher.processOrder(orderBuy2)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual orderBuy1
    orderBook.top(Sell) should be (None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy)  should be (None)
    orderBook.top(Sell) should be (None)

    verify(orderSender, times(2)).send(argThat(new OrderExecutionResponseArgumentMatcher(size / 2, price)))
    verify(orderSender, never).send(argThat(new OrderRejectionResponseArgumentMatcher))
  }

  "OrderMatcher" should "generate 2 deals: 2 buy orders, 1 sell order and 1 rejection" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy1 = limitOrder(side = Buy, limit = price, size = size, sequence = 1)
    val orderBuy2 = limitOrder(side = Buy, limit = price, size = size, sequence = 2)
    val orderSell = marketOrder(side = Sell, size = size * 3, sequence = 3)

    orderBuy1.qty shouldEqual orderSell.qty / 3

    orderMatcher.processOrder(orderBuy1)
    orderMatcher.processOrder(orderBuy2)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual orderBuy1
    orderBook.top(Sell) should be (None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy)  should be (None)
    orderBook.top(Sell) should be (None)

    verify(orderSender, times(2)).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
    verify(orderSender).send(argThat(new OrderRejectionResponseArgumentMatcher))
  }

  "OrderMatcher" should "should add 2 counter order to books without any deals" in {
    val execIDSequencer = new Sequencer
    val orderBook = spy(new OrderBook)
    val orderSender = mock[OrderProcessingResponseSender]
    val orderMatcher = new OrderMatcher(execIDSequencer, orderBook, orderSender, new DefaultClock)

    val priceBuy = 4.30
    val priceSell = 4.40
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = priceBuy, size = size, sequence = 1)
    val orderSell = limitOrder(side = Sell, limit = priceSell, size = size, sequence = 2)


    orderMatcher.processOrder(orderBuy)
    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy)  should not be None
    orderBook.top(Buy).get shouldEqual orderBuy
    orderBook.top(Sell) should not be None
    orderBook.top(Sell).get shouldEqual orderSell

    verify(orderSender, never).send(any(classOf[OrderProcessingResponse]))
  }
}

class OrderExecutionResponseArgumentMatcher(dealSize: Double, dealPrice: Double) extends ArgumentMatcher[OrderProcessingResponse] {

  def matchesDealSize(dealDone: DealDone): Boolean = dealDone.dealSize == dealSize
  def matchesDealPrice(dealDone: DealDone): Boolean = dealDone.dealPrice == dealPrice

  override def matches(argument: scala.Any): Boolean = {
    if(!argument.isInstanceOf[OrderExecutionResponse]) false
    else {
      val dealDone = argument.asInstanceOf[OrderExecutionResponse].dealDone
      matchesDealSize(dealDone) && matchesDealPrice(dealDone)
      }
  }
}

class OrderRejectionResponseArgumentMatcher extends ArgumentMatcher[OrderProcessingResponse] {
  override def matches(argument: scala.Any): Boolean = argument.isInstanceOf[OrderRejectionResponse]
}
