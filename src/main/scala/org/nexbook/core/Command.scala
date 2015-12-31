package org.nexbook.core

/**
  * Created by milczu on 12/23/15.
  */
trait Command[T] {

  def payload: T
}
