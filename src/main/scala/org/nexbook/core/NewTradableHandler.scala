package org.nexbook.core

import org.nexbook.domain.NewTradable

/**
 * Created by milczu on 09.12.15
 */
trait NewTradableHandler[T <: NewTradable] {

  def handle(tradable: T)
}
