package org.nexbook.app

import com.softwaremill.macwire._
import org.nexbook.core.Handler
import org.nexbook.fix.FixOrderConverter
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.orderchange.OrderChangeCommand
import org.nexbook.repository._
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.DefaultClock

/**
  * Created by milczu on 12/22/15.
  */
trait BasicComponentProvider {

  lazy val orderRepositoryResolver = new OrderRepositoryResolver {
	val orderInMemoryRepository: OrderInMemoryRepository = if (AppConfig.repositoryCollectionType == Mutable) new mutable.OrderInMemoryRepository else new immutable.OrderInMemoryRepository
	val orderDatabaseRepository = wire[OrderDatabaseRepository]
	val orderChainedRepository: OrderChainedRepository = new OrderChainedRepository(orderInMemoryRepository, orderDatabaseRepository)

	override def orderRepository: OrderRepository = if(AppConfig.dbPersist) orderChainedRepository else orderInMemoryRepository

	override def inMemoryRepository: OrderInMemoryRepository = orderInMemoryRepository

	override def databaseRepository: OrderDatabaseRepository = orderDatabaseRepository
  }

  lazy val orderDatabaseRepository = orderRepositoryResolver.databaseRepository
  lazy val executionDatabaseRepository = wire[ExecutionDatabaseRepository]
  lazy val sequencerFactory = wire[SequencerFactory]
  lazy val clock = new DefaultClock
  lazy val matchingEnginesRepository = wire[MatchingEnginesRepository]
  lazy val fixOrderConverter = wire[FixOrderConverter]

  def module: Module

  def orderBookResponseHandlers: List[Handler[OrderBookResponse]] = module.orderBookResponseHandlers

  def orderChangeChandlers: List[Handler[OrderChangeCommand]] = module.orderChangeHandlers

}
