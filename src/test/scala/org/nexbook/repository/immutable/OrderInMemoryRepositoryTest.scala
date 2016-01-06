package org.nexbook.repository.immutable

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain.{Buy, MarketOrder}
import org.scalatest._

import scala.None
import scala.collection.immutable.List

class OrderInMemoryRepositoryTest extends WordSpecLike with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  "A new OrderRepository" should {
	"be empty" in {
	  val repository = new OrderInMemoryRepository
	  repository.findAll should be(List.empty)
	}
  }

  "OrderRepository" should {
	"not contains duplicates" in {
	  val repository = new OrderInMemoryRepository
	  val order = new MarketOrder(1, "EUR/USD", "client1", Buy, 100, "NEX", now, "1")

	  repository add order
	  repository add order

	  val orders = repository.findAll
	  orders should have size 1
	  orders should be(List(order))
	}

	"find orders by tradeID" in {
	  val repository = new OrderInMemoryRepository
	  val now = DateTime.now(DateTimeZone.UTC)

	  val order1 = new MarketOrder(1, "EUR/USD", "client1", Buy, 100, "NEX", now.minusSeconds(10), "1")

	  repository add order1

	  val orders = repository.findAll
	  orders should have size 1

	  repository.findById(1) shouldBe defined
	  repository.findById(1) shouldBe Some(order1)

	  repository.findById(2) shouldBe None
	}
  }

}
