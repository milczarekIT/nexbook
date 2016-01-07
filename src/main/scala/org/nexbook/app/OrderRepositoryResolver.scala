package org.nexbook.app

import org.nexbook.repository.{OrderDatabaseRepository, OrderInMemoryRepository, OrderRepository}

/**
  * Created by milczu on 07.01.16.
  */
trait OrderRepositoryResolver {

  def orderRepository: OrderRepository

  def inMemoryRepository: OrderInMemoryRepository

  def databaseRepository: OrderDatabaseRepository

}
