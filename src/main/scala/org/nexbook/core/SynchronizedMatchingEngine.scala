package org.nexbook.core

import org.nexbook.domain._

trait SynchronizedMatchingEngine extends DefaultMatchingEngine {

  override def processOrder(order: Order) = this.synchronized {
	super.processOrder(order)
  }

}

