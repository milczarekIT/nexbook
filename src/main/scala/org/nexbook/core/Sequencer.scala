package org.nexbook.core

import java.util.concurrent.atomic.AtomicLong

/**
 * Created by milczu on 16.12.14.
 */
class Sequencer {

  val counter = new AtomicLong

  def nextValue = counter.incrementAndGet

}
