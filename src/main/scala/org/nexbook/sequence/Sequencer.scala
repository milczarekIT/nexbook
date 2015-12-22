package org.nexbook.sequence

import java.util.concurrent.atomic.AtomicLong

/**
  * Created by milczu on 16.12.14.
  */
class Sequencer(initValue: Long) {

  def this() = this(0)

  val counter = new AtomicLong(initValue)

  def nextValue = counter.incrementAndGet

}
