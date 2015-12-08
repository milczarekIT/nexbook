package org.nexbook.repository

import org.nexbook.domain.Order



class OrderChainedRepository(inMemoryRepository: OrderInMemoryRepository, databaseRepository: OrderDatabaseRepository) extends OrderRepository {

  def add(order: Order) = {
    inMemoryRepository add order
    databaseRepository add order
  }

  def findAll: List[Order] = inMemoryRepository findAll

}
