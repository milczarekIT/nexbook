package org.nexbook.repository

import org.nexbook.domain.Order

/**
  * Created by milczu on 03.01.16.
  */
trait OrderInMemoryRepository extends OrderRepository {

  def findAll: List[Order]

  def count: Int
}
