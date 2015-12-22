package org.nexbook.repository

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain.{Buy, MarketOrder}
import org.scalatest._

import scala.collection.immutable.List

class OrderInMemoryRepositoryTest extends FlatSpec with Matchers {

  val now = DateTime.now(DateTimeZone.UTC)

  "A new OrderRepository" should "be empty" in {
	val repository = new OrderInMemoryRepository
	repository.findAll should be(List.empty)
  }

  "OrderRepository" should "not contains duplicates" in {
	val repository = new OrderInMemoryRepository
	val order = new MarketOrder(1, "EUR/USD", "client1", Buy, 100, "NEX", now, "1")

	repository add order
	repository add order

	val orders = repository.findAll
	orders should have size 1
	orders should be(List(order))
  }

  "OrderRepository" should "be orderdered by timestamp desc" in {
	val repository = new OrderInMemoryRepository
	val now = DateTime.now(DateTimeZone.UTC)

	val order1 = new MarketOrder(1, "EUR/USD", "client1", Buy, 100, "NEX", now.minusSeconds(10), "1")
	val order2 = new MarketOrder(2, "EUR/USD", "client1", Buy, 100, "NEX", now.minusSeconds(3), "1")

	repository add order1
	repository add order2

	val orders = repository.findAll
	orders should have size 2
	orders should be(List(order2, order1))
  }
}
