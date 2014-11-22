package org.nexbook.generator

import java.util.concurrent.atomic.AtomicLong


class SequenceGenerator(initValue: Long) {
  val counter = new AtomicLong(initValue)

  def this() = this(0)

  def nextValue = counter.incrementAndGet()
}
