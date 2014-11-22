package org.nexbook.generator

import java.util.concurrent.atomic.AtomicLong

class TradeIdGenerator(initValue: String) {
  val REGEX_PATTERN = "[A-Z]{3}[0-9]{5}"
  val currentValue = new AtomicLong(toLong(initValue))
  val seq = 'A' to 'Z'

  require(initValue.matches(REGEX_PATTERN), "Initial value must match regex: " + REGEX_PATTERN)

  def this() = this("AAA00000")

  def toLong(str: String): Long = {
  0
  }
}
