package org.nexbook.core

/**
  * Created by milczu on 09.12.15
  */
trait Handler[T] {

  def handle(o: T)
}
