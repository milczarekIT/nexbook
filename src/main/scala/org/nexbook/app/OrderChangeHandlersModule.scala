package org.nexbook.app

import org.nexbook.core.Handler
import org.nexbook.orderchange.OrderChangeCommand

/**
  * Created by milczu on 12/23/15.
  */
trait OrderChangeHandlersModule {

  def orderChangeHandlers: List[Handler[OrderChangeCommand]]
}
