package org.nexbook.app

import org.nexbook.neworderhandler.NewOrderHandler

/**
  * Created by milczu on 12/21/15.
  */
trait OrderHandlersModule {

  def newOrderHandlers: List[NewOrderHandler]

}
