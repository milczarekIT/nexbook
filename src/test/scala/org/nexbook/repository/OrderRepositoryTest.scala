package org.nexbook.repository

import org.nexbook.domain.{Buy, MarketOrder}
import org.scalatest._

import scala.collection.immutable.List

class OrderRepositoryTest extends FlatSpec with Matchers {

  "A new OrderRepository" should "be empty" in {
    val repository = new OrderRepository
    repository.getOrders should be(List.empty)
  }

  "OrderRepository" should "not contains duplicates" in {
    val repository = new OrderRepository
    val order = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")

    repository add order
    repository add order

    val orders = repository.getOrders
    orders should have size (1)
    orders should be(List(order))
  }

  "OrderRepository" should "be orderdered by timestamo desc" in {
    val repository = new OrderRepository
    val order1 = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")
    val order2 = new MarketOrder("1", "EUR/USD", "client1", Buy, 100, "NEX")

    repository add order1
    repository add order2

    val orders = repository.getOrders
    orders should have size (2)
    orders should be(List(order2, order1))
  }
}
