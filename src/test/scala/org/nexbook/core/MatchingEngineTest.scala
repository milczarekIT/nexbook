package org.nexbook.core

import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.ArgumentMatcher
import org.mockito.Matchers._
import org.mockito.Mockito.{never, spy, times, verify}
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.handler.OrderBookResponseHandler
import org.nexbook.orderbookresponsehandler.response.{OrderBookResponse, OrderExecutionResponse, OrderRejectionResponse}
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository, OrderInMemoryRepository}
import org.nexbook.sequence.SequencerFactory
import org.scalatest.mock.MockitoSugar._
import org.scalatest.{Matchers, WordSpecLike}


/**
  * Created by milczu on 08.12.15.
  */
class MatchingEngineTest extends WordSpecLike with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  def limitOrder(side: Side = Buy, limit: Double = 4.32, size: Double = 100, sequence: Long = 1) = LimitOrder(sequence, "EUR/PLN", "cl1", side, size, limit, "FIX1", now, "1", Limit)

  def marketOrder(side: Side = Buy, size: Double = 100, sequence: Long = 2) = MarketOrder(sequence, "EUR/PLN", "cl2", side, size, "FIX2", now, "2", Market)

  val orderInMemoryRepository = mock[OrderInMemoryRepository]
  val sequencerFactory = new SequencerFactory(mock[OrderDatabaseRepository], mock[ExecutionDatabaseRepository])

  "Empty MatchingEngine" should {
	"send rejection for first MarketOrder" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val marketOrder1 = marketOrder()

	  matchingEngine.processOrder(marketOrder1)

	  orderBook.top(Buy) should be(None)
	  orderBook.top(Sell) should be(None)

	  //verify(orderBookResponseHandler).handle(any(classOf[OrderRejectionResponse]))
	  verify(orderBook, never).add(any(classOf[LimitOrder]))
	}

	"should add original LimitOrder to OrderBook" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val order = limitOrder()

	  matchingEngine.processOrder(order)

	  orderBook.top(Buy) should not be None
	  orderBook.top(Buy).get shouldEqual order
	  orderBook.top(Sell) should be(None)

	  verify(orderBookResponseHandler, never).handle(any(classOf[OrderRejectionResponse]))
	  verify(orderBook, times(1)).add(any(classOf[LimitOrder]))
	}
  }


  "MatchingEngine" should {
	"generate one deal: 2 orders on both sides with same size and price" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val price = 4.32
	  val size = 100

	  val orderBuy = limitOrder(side = Buy, limit = price, size = size)
	  val orderSell = limitOrder(side = Sell, limit = price, size = size)

	  orderBuy.qty shouldEqual orderSell.qty
	  orderBuy.limit shouldEqual orderSell.limit

	  matchingEngine.processOrder(orderBuy)

	  orderBook.top(Buy) should not be None
	  orderBook.top(Buy).get shouldEqual orderBuy
	  orderBook.top(Sell) should be(None)

	  matchingEngine.processOrder(orderSell)

	  orderBook.top(Buy) should be(None)
	  orderBook.top(Sell) should be(None)

	  //verify(orderBookResponseHandler, times(2)).handle(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
	}

	"generate one deal: 2 orders on both sides with same size. First: Limit, Second: Market" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val price = 4.32
	  val size = 100

	  val orderBuy = limitOrder(side = Buy, limit = price, size = size)
	  val orderSell = marketOrder(side = Sell, size = size)

	  orderBuy.qty shouldEqual orderSell.qty

	  matchingEngine.processOrder(orderBuy)

	  orderBook.top(Buy) should not be None
	  orderBook.top(Buy) should contain(orderBuy)
	  orderBook.top(Sell) shouldBe None

	  matchingEngine.processOrder(orderSell)

	  orderBook.top(Buy) shouldBe None
	  orderBook.top(Sell) shouldBe None

	  //verify(orderBookResponseHandler, times(2)).handle(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
	}

	"generate 2 deals: 2 buy orders, 1 sell order, with same size. Size is matching, Fulfill" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val price = 4.32
	  val size = 100

	  val orderBuy1 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 1)
	  val orderBuy2 = limitOrder(side = Buy, limit = price, size = size / 2, sequence = 2)
	  val orderSell = marketOrder(side = Sell, size = size, sequence = 3)

	  orderBuy1.qty shouldEqual orderSell.qty / 2

	  matchingEngine.processOrder(orderBuy1)
	  matchingEngine.processOrder(orderBuy2)

	  orderBook.top(Buy) should not be None
	  orderBook.top(Buy).get shouldEqual orderBuy1
	  orderBook.top(Sell) should be(None)

	  matchingEngine.processOrder(orderSell)

	  orderBook.top(Buy) should be(None)
	  orderBook.top(Sell) should be(None)

	  //verify(orderBookResponseHandler, times(4)).handle(argThat(new OrderExecutionResponseArgumentMatcher(size / 2, price)))
	  //verify(orderBookResponseHandler, never).handle(argThat(new OrderRejectionResponseArgumentMatcher))
	}

	"generate 2 deals: 2 buy orders, 1 sell order and 1 rejection" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val price = 4.32
	  val size = 100

	  val orderBuy1 = limitOrder(side = Buy, limit = price, size = size, sequence = 1)
	  val orderBuy2 = limitOrder(side = Buy, limit = price, size = size, sequence = 2)
	  val orderSell = marketOrder(side = Sell, size = size * 3, sequence = 3)

	  orderBuy1.qty shouldEqual orderSell.qty / 3

	  matchingEngine.processOrder(orderBuy1)
	  matchingEngine.processOrder(orderBuy2)

	  orderBook.top(Buy) should not be None
	  orderBook.top(Buy).get shouldEqual orderBuy1
	  orderBook.top(Sell) should be(None)

	  matchingEngine.processOrder(orderSell)

	  orderBook.top(Buy) should be(None)
	  orderBook.top(Sell) should be(None)

	  //verify(orderBookResponseHandler, times(4)).handle(argThat(new OrderExecutionResponseArgumentMatcher(size, price)))
	  //verify(orderBookResponseHandler).handle(argThat(new OrderRejectionResponseArgumentMatcher))
	}

	"should add 2 counter order to books without any deals" in {
	  val orderBook = spy(new OrderBook)
	  val orderBookResponseHandler = mock[OrderBookResponseHandler]
	  val orderChangeHandler: Handler[OrderChangeCommand] = mock[Handler[OrderChangeCommand]]
	  val matchingEngine = new MatchingEngine(orderInMemoryRepository, sequencerFactory, orderBook, List(orderBookResponseHandler), List(orderChangeHandler))

	  val priceBuy = 4.30
	  val priceSell = 4.40
	  val size = 100

	  val orderBuy = limitOrder(side = Buy, limit = priceBuy, size = size, sequence = 1)
	  val orderSell = limitOrder(side = Sell, limit = priceSell, size = size, sequence = 2)


	  matchingEngine.processOrder(orderBuy)
	  matchingEngine.processOrder(orderSell)

	  orderBook.top(Buy) shouldNot be(None)
	  orderBook.top(Buy) should contain(orderBuy)
	  orderBook.top(Sell) shouldNot be(None)
	  orderBook.top(Sell) should contain(orderSell)

	  verify(orderBookResponseHandler, never).handle(any(classOf[OrderBookResponse]))
	}
  }
}

class OrderExecutionResponseArgumentMatcher(dealQty: Double, dealPrice: Double) extends ArgumentMatcher[OrderBookResponse] {

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

class OrderRejectionResponseArgumentMatcher extends ArgumentMatcher[OrderBookResponse] {
  override def matches(argument: scala.Any): Boolean = argument.isInstanceOf[OrderRejectionResponse]
}
