package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.core.Handler
import org.nexbook.fix.FixOrderConverter
import org.nexbook.neworderhandler.{OrderCancelHandler, OrderHandler}
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.repository._
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.DefaultClock

/**
  * Created by milczu on 12/22/15.
  */
trait BasicComponentProvider {

  lazy val orderInMemoryRepository = wire[OrderInMemoryRepository]
  lazy val orderDatabaseRepository = wire[OrderDatabaseRepository]
  lazy val orderRepository = wire[OrderChainedRepository]
  lazy val executionDatabaseRepository = wire[ExecutionDatabaseRepository]
  lazy val sequencerFactory = wire[SequencerFactory]
  lazy val clock = new DefaultClock
  def module: Module
  def ordBookH: List[Handler[OrderBookResponse]] = module.orderResponseHandlers
  lazy val orderMatchersRepository = wire[OrderMatchersRepository]
  lazy val orderHandler: OrderHandler = wire[OrderHandler]
  lazy val orderCancelHandler = wire[OrderCancelHandler]
  lazy val fixOrderConverter = wire[FixOrderConverter]

}