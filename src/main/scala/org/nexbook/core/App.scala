package org.nexbook.core

import com.softwaremill.macwire._
import com.typesafe.config.ConfigFactory
import org.nexbook.fix.{FixOrderHandler, FixOrderHandlerRunner}
import org.nexbook.handler.{GeneralResponseHandler, ResponseFixResponseSender, ResponseHandler, ResponseJsonLoggingHandler}
import org.nexbook.orderprocessing.OrderProcessingResponseLifecycleFactory
import org.nexbook.orderprocessing.actors.ActorsOrderProcessingResponseLifecycleFactory
import org.nexbook.orderprocessing.publishsubscribe.PubSubOrderProcessingResponseLifecycleFactory
import org.nexbook.repository.{OrderBookRepository, OrderDatabaseRepository, OrderRepository}
import org.nexbook.utils.DefaultClock
import org.slf4j.LoggerFactory

object App {

  val LOGGER = LoggerFactory.getLogger(classOf[App])

  val config = ConfigFactory.load()
  val fixConfigPath = config.getString("org.nexbook.fix.config.path")

  val orderBookRepository = wire[OrderBookRepository]
  val orderRepository = wire[OrderRepository]
  val orderDatabaseRepository = wire[OrderDatabaseRepository]
  val mode = config.getString("org.nexbook.mode")
  val generalResponseHandler = wire[GeneralResponseHandler]
  val clock = new DefaultClock
  val orderHandler = wire[OrderHandler]

  def resolveLifeCycleFactory: OrderProcessingResponseLifecycleFactory = mode match {
    case "PubSub" => wire[PubSubOrderProcessingResponseLifecycleFactory]
    case "Actors" => wire[ActorsOrderProcessingResponseLifecycleFactory]
    case _ => throw new IllegalArgumentException
  }

  def responseHandlers: List[ResponseHandler] = List(wire[ResponseJsonLoggingHandler], wire[ResponseFixResponseSender])

  def main(args: Array[String]) {
    LOGGER.info("NexBook starting")
    LOGGER.debug("Mode: {}", mode)

    val fixOrderHandler = wire[FixOrderHandler]
    val fixOrderHandlerRunner = new FixOrderHandlerRunner(fixOrderHandler, fixConfigPath)
    fixOrderHandlerRunner.run
  }
}
