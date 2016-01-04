package org.nexbook.repository.mutable

import org.nexbook.domain.Order

class OrderInMemoryParRepository extends OrderInMemoryRepository {

  override def findByClOrdId(clOrdId: String): Option[Order] = orders.values.par.find(_.clOrdId == clOrdId)

}
