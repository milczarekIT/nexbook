package org.nexbook.repository

import org.nexbook.domain.{Order, OrderStatus}


class OrderChainedRepository(inMemoryRepository: OrderInMemoryRepository, databaseRepository: OrderDatabaseRepository) extends OrderRepository {

  def add(order: Order) = {
	inMemoryRepository add order
	databaseRepository add order
  }

  def findAll: List[Order] = inMemoryRepository findAll

  override def findBy(clOrdId: String, connector: String): Option[Order] = {
	inMemoryRepository.findBy(clOrdId, connector) orElse databaseRepository.findBy(clOrdId, connector)
  }

  override def updateStatus(tradeID: Long, newStatus: OrderStatus, oldStatus: OrderStatus): Boolean = {
	val resInMemory = inMemoryRepository.updateStatus(tradeID, newStatus, oldStatus)
	if (resInMemory) {
	  val resDb = databaseRepository.updateStatus(tradeID, newStatus, oldStatus)
	  if (resDb) true
	  else {
		inMemoryRepository.updateStatus(tradeID, oldStatus, newStatus)
		false
	  }
	}
	else false
  }
}
