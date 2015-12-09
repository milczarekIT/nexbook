package org.nexbook.core

import com.softwaremill.macwire._
import com.typesafe.config.ConfigFactory
import org.nexbook.fix.{FixOrderHandler, FixOrderHandlerRunner}
import org.nexbook.handler._
import org.nexbook.orderprocessing.actors.ActorsOrderProcessingResponseLifecycleFactory
import org.nexbook.orderprocessing.publishsubscribe.PubSubOrderProcessingResponseLifecycleFactory
import org.nexbook.orderprocessing.{OrderProcessingResponseLifecycleFactory, OrderProcessingResponseSender}
import org.nexbook.repository._
import org.nexbook.sequence.SequencerFactory
import org.nexbook.utils.DefaultClock
import org.slf4j.LoggerFactory

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val config = ConfigFactory.load()
  val fixConfigPath = config.getString("org.nexbook.fix.config.path")

  val orderInMemoryRepository = wire[OrderInMemoryRepository]
  val orderDatabaseRepository = wire[OrderDatabaseRepository]
  val orderRepository = wire[OrderChainedRepository]
  val executionDatabaseRepository = wire[ExecutionDatabaseRepository]
  val mode = config.getString("org.nexbook.mode")
  val generalResponseHandler = wire[GeneralResponseHandler]
  val clock = new DefaultClock
  val sequencerFactory = wire[SequencerFactory]
  val orderMatchersRepository = wire[OrderMatchersRepository]
  val orderHandler = wire[OrderHandler]
  val orderCancelHandler = wire[OrderCancelHandler]

  def resolveLifeCycleFactory: OrderProcessingResponseLifecycleFactory = mode match {
    case "PubSub" => wire[PubSubOrderProcessingResponseLifecycleFactory]
    case "Actors" => wire[ActorsOrderProcessingResponseLifecycleFactory]
    case _ => throw new IllegalArgumentException
  }

  def orderProcessingResponseSender: OrderProcessingResponseSender = resolveLifeCycleFactory.sender

  def responseHandlers: List[ResponseHandler] = List(wire[ResponseJsonLoggingHandler], wire[ResponseTradeDatabaseSaver], wire[ResponseFixResponseSender])

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    LOGGER.debug("Mode: {}", mode)

    val fixOrderHandler = wire[FixOrderHandler]
    val fixOrderHandlerRunner = new FixOrderHandlerRunner(fixOrderHandler, fixConfigPath)
    fixOrderHandlerRunner.run
  }
}
