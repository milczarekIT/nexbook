package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.ArgumentMatcher
import org.mockito.Matchers._
import org.mockito.Mockito.{never, spy, times, verify}
import org.nexbook.domain._
import org.nexbook.orderprocessing.ProcessingResponseSender
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderProcessingResponse, OrderRejectionResponse}
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository, OrderInMemoryRepository}
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.DefaultClock
import org.scalatest.mock.MockitoSugar._
import org.scalatest.{FlatSpec, Matchers}


/**
 * Created by milczu on 08.12.15.
 */
class OrderMatcherTest extends FlatSpec with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  def limitOrder(side: Side = Buy, limit: Double = 4.32, size: Double = 100, sequence: Long = 1) = LimitOrder(sequence, "EUR/PLN", "cl1", side, size, limit, "FIX1", now, "1", Limit)

  def marketOrder(side: Side = Buy, size: Double = 100, sequence: Long = 2) = MarketOrder(sequence, "EUR/PLN", "cl2", side, size, "FIX2", now, "2", Market)

  val orderInMemoryRepository = mock[OrderInMemoryRepository]
  val sequencerFactory = new SequencerFactory(mock[OrderDatabaseRepository], mock[ExecutionDatabaseRepository])

  "Empty OrderMatcher" should "send rejection for first MarketOrder" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val marketOrder1 = marketOrder()

    orderMatcher.processOrder(marketOrder1)

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)

    verify(orderSender).send(any(classOf[OrderProcessingResponse]))
    verify(orderBook, never).add(any(classOf[LimitOrder]))
  }

  "Empty OrderMatcher" should "should add original LimitOrder to OrderBook" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val order = limitOrder()

    orderMatcher.processOrder(order)

    orderBook.top(Buy) should not be None
    orderBook.top(Buy).get shouldEqual order
    orderBook.top(Sell) should be(None)

    verify(orderSender, never).send(any(classOf[OrderRejectionResponse]))
    verify(orderBook, times(1)).add(any(classOf[LimitOrder]))
  }

  "OrderMatcher" should "generate one deal: 2 orders on both sides with same size and price" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = price, size = size)
    val orderSell = limitOrder(side = Sell, limit = price, size = size)

    orderBuy.qty shouldEqual orderSell.qty
    orderBuy.limit shouldEqual orderSell.limit

    orderMatcher.processOrder(orderBuy)

    orderBook.top(Buy) should not be None
    orderBook.top(Buy).get shouldEqual orderBuy
    orderBook.top(Sell) should be(None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)

    verify(orderSender, times(2)).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
  }

  "OrderMatcher" should "generate one deal: 2 orders on both sides with same size. First: Limit, Second: Market" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = price, size = size)
    val orderSell = marketOrder(side = Sell, size = size)

    orderBuy.qty shouldEqual orderSell.qty

    orderMatcher.processOrder(orderBuy)

    orderBook.top(Buy) should not be None
    orderBook.top(Buy) should contain(orderBuy)
    orderBook.top(Sell) shouldBe None

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy) shouldBe None
    orderBook.top(Sell) shouldBe None

    verify(orderSender, times(2)).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
  }

  "OrderMatcher" should "generate 2 deals: 2 buy orders, 1 sell order, with same size. Size is matching, Fulfill" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy1 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 1)
    val orderBuy2 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 2)
    val orderSell = marketOrder(side = Sell, size = size, sequence = 3)

    orderBuy1.qty shouldEqual orderSell.qty / 2

    orderMatcher.processOrder(orderBuy1)
    orderMatcher.processOrder(orderBuy2)

    orderBook.top(Buy) should not be None
    orderBook.top(Buy).get shouldEqual orderBuy1
    orderBook.top(Sell) should be(None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)

    verify(orderSender, times(4)).send(argThat(new OrderExecutionResponseArgumentMatcher(size / 2, price)))
    verify(orderSender, never).send(argThat(new OrderRejectionResponseArgumentMatcher))
  }

  "OrderMatcher" should "generate 2 deals: 2 buy orders, 1 sell order and 1 rejection" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val price = 4.32
    val size = 100

    val orderBuy1 = limitOrder(side = Buy, limit = price, size = size, sequence = 1)
    val orderBuy2 = limitOrder(side = Buy, limit = price, size = size, sequence = 2)
    val orderSell = marketOrder(side = Sell, size = size * 3, sequence = 3)

    orderBuy1.qty shouldEqual orderSell.qty / 3

    orderMatcher.processOrder(orderBuy1)
    orderMatcher.processOrder(orderBuy2)

    orderBook.top(Buy) should not be None
    orderBook.top(Buy).get shouldEqual orderBuy1
    orderBook.top(Sell) should be(None)

    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy) should be(None)
    orderBook.top(Sell) should be(None)

    verify(orderSender, times(4)).send(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
    verify(orderSender).send(argThat(new OrderRejectionResponseArgumentMatcher))
  }

  "OrderMatcher" should "should add 2 counter order to books without any deals" in {
    val orderBook = spy(new OrderBook)
    val orderSender = mock[ProcessingResponseSender]
    val orderMatcher = new OrderMatcher(orderInMemoryRepository, sequencerFactory, orderBook, orderSender, new DefaultClock)

    val priceBuy = 4.30
    val priceSell = 4.40
    val size = 100

    val orderBuy = limitOrder(side = Buy, limit = priceBuy, size = size, sequence = 1)
    val orderSell = limitOrder(side = Sell, limit = priceSell, size = size, sequence = 2)


    orderMatcher.processOrder(orderBuy)
    orderMatcher.processOrder(orderSell)

    orderBook.top(Buy) shouldNot be(None)
    orderBook.top(Buy) should contain(orderBuy)
    orderBook.top(Sell) shouldNot be(None)
    orderBook.top(Sell) should contain(orderSell)

    verify(orderSender, never).send(any(classOf[OrderProcessingResponse]))
  }
}

class OrderExecutionResponseArgumentMatcher(dealQty: Double, dealPrice: Double) extends ArgumentMatcher[OrderProcessingResponse] {

  def matchesDealSize(dealDone: OrderExecution): Boolean = dealDone.executionQty == dealQty

  def matchesDealPrice(dealDone: OrderExecution): Boolean = dealDone.executionPrice == dealPrice

  override def matches(argument: scala.Any): Boolean = {
    if (!argument.isInstanceOf[OrderExecutionResponse]) false
    else {
      val orderExecution = argument.asInstanceOf[OrderExecutionResponse].orderExecution
      matchesDealSize(orderExecution) && matchesDealPrice(orderExecution)
    }
  }
}

class OrderRejectionResponseArgumentMatcher extends ArgumentMatcher[OrderProcessingResponse] {
  override def matches(argument: scala.Any): Boolean = argument.isInstanceOf[OrderRejectionResponse]
}
