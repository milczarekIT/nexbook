package org.nexbook.neworderhandler

import org.nexbook.domain.{NewOrder, NewOrderCancel}

/**
  * Created by milczu on 05.01.16.
  */
trait NewOrderHandler {

  def handleNewOrder(o: NewOrder)

  def handleNewOrderCancel(c: NewOrderCancel)
}
