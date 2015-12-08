package org.nexbook.repository

import org.nexbook.domain.Order

/**
 * Created by milczu on 08.12.15.
 */
trait OrderRepository {

  def add(order: Order)

  def findAll: List[Order]
}
