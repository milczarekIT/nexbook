package org.nexbook.repository

import org.nexbook.domain.{Order, OrderStatus}


class OrderChainedRepository(inMemoryRepository: OrderInMemoryRepository, databaseRepository: OrderDatabaseRepository) extends OrderRepository {

  def add(order: Order) = {
	inMemoryRepository add order
	databaseRepository add order
  }

  override def findByClOrdId(clOrdId: String): Option[Order] = inMemoryRepository.findByClOrdId(clOrdId) orElse databaseRepository.findByClOrdId(clOrdId)

  override def findById(tradeID: Long): Option[Order] = inMemoryRepository.findById(tradeID) orElse databaseRepository.findById(tradeID)

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
