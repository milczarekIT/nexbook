package org.nexbook.core

import org.nexbook.domain.Order

/**
  * Created by milczu on 08.01.16.
  */
trait MatchingEngine {

  def processOrder(order: Order)
}
