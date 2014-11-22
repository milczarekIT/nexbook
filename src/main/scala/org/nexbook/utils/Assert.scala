package org.nexbook.utils


object Assert {

  def isTrue(expression: Boolean) = if (!expression) throw new IllegalArgumentException
}


