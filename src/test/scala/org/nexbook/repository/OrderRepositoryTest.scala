package org.nexbook.repository

import org.joda.time.{DateTimeZone, DateTime}
import org.nexbook.domain.{Buy, MarketOrder}
import org.scalatest._

import scala.collection.immutable.List

class OrderRepositoryTest extends FlatSpec with Matchers {

  "A new OrderRepository" should "be empty" in {
    val repository = new OrderRepository
    repository.findAll should be(List.empty)
  }

  "OrderRepository" should "not contains duplicates" in {
    val repository = new OrderRepository
    val order = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")

    repository add order
    repository add order

    val orders = repository.findAll
    orders should have size (1)
    orders should be(List(order))
  }

  "OrderRepository" should "be orderdered by timestamp desc" in {
    val repository = new OrderRepository
    val now = DateTime.now(DateTimeZone.UTC)

    val order1 = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")
    order1.setTimestamp(now.minusSeconds(10))
    val order2 = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")
    order2.setTimestamp(now.minusSeconds(3))

    repository add order1
    repository add order2

    val orders = repository.findAll
    orders should have size (2)
    orders should be(List(order2, order1))
  }
}
