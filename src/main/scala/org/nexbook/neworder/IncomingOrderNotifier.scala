package org.nexbook.neworder

import org.nexbook.domain.NewOrder

/**
 * Created by milczu on 11.12.15
 */
trait IncomingOrderNotifier {

  def notify(newOrder: NewOrder)
}
