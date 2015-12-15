package org.nexbook.core

import com.softwaremill.macwire._
import com.typesafe.config.ConfigFactory
import org.nexbook.fix.{FixOrderConverter, FixMessageHandler, FixEngineRunner}
import org.nexbook.neworder.IncomingOrderHandlerModule
import org.nexbook.neworder.actors.ActorsIncomingOrderHandlerModule
import org.nexbook.orderprocessing.handler._
import org.nexbook.orderprocessing.actors.ActorsProcessingResponseModule
import org.nexbook.orderprocessing.publishsubscribe.PubSubProcessingResponseModule
import org.nexbook.orderprocessing.{ProcessingResponseHandler, ProcessingResponseModule, ProcessingResponseSender}
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
  val clock = new DefaultClock
  val sequencerFactory = wire[SequencerFactory]
  val orderMatchersRepository = wire[OrderMatchersRepository]
  val orderHandler: OrderHandler = wire[OrderHandler]
  val orderCancelHandler = wire[OrderCancelHandler]
  val fixOrderConverter = wire[FixOrderConverter]

  def resolveLifeCycleFactory: ProcessingResponseModule = mode match {
    case "PubSub" => wire[PubSubProcessingResponseModule]
    case "Actors" => wire[ActorsProcessingResponseModule]
    case _ => throw new IllegalArgumentException
  }

  def orderProcessingResponseSender: ProcessingResponseSender = resolveLifeCycleFactory.responseSender

  def responseHandlers: List[ProcessingResponseHandler] = List(wire[JsonFileLogger], wire[TradeDatabaseSaver], wire[FixMessageResponseSender])

  def incomingOrderHandlerModule: IncomingOrderHandlerModule = wire[ActorsIncomingOrderHandlerModule]

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    LOGGER.debug("Mode: {}", mode)

    val fixMessageHandler: FixMessageHandler = wire[FixMessageHandler]
    val fixEngineRunner = new FixEngineRunner(fixMessageHandler, fixConfigPath)
    fixEngineRunner.run
  }
}
