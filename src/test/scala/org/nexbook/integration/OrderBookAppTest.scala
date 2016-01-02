package org.nexbook.integration

import org.nexbook.app.OrderBookApp
import org.nexbook.fix.FixMessageHandler
import org.nexbook.tags.IntegrationTest
import org.scalatest.concurrent.Timeouts
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import quickfix.field.MsgType
import quickfix.{Message, SessionID}

/**
  * Created by milczu on 1/2/16.
  */
class OrderBookAppTest extends FlatSpec with Matchers with Timeouts {

  System.setProperty("config.name", "nexbook")
  val logger = LoggerFactory.getLogger(classOf[OrderBookAppTest])
  val fixMsgReader = new FixMessageReader
  val dbCollections = List("orders", "executions")
  val expectedCount = 100000

  import org.scalatest.time.SpanSugar._

  "testData" should "contains 95248 NewOrderSingle and 4752 OrderCancelRequest" in {
	val testDataPath = "src/test/resources/data/orders8.fix"
	logger.info(s"load test data: $testDataPath")
	val messages: List[Message] = fixMsgReader.readAll(testDataPath).map(_._1)

	messages should have size expectedCount

	val countsByMsgType: Map[String, Int] = messages.groupBy(m => m.getHeader.getField(new MsgType()).getValue).map(e => e._1 -> e._2.size).toMap

	countsByMsgType("D") shouldBe equal(95248)
	countsByMsgType("F") shouldBe equal(4752)

  }

  "OrderBook" should "work fast!" taggedAs IntegrationTest in {
	failAfter(120 seconds) {
	  logger.info("Test run!")
	  for (dbCollection <- dbCollections) {
		val count = MongodbTestUtils.count(dbCollection)
		logger.info(s"Count $dbCollection : $count")
		MongodbTestUtils.dropCollection(dbCollection)
		val count2 = MongodbTestUtils.count(dbCollection)
		logger.info(s"Count2 $dbCollection : $count2")
	  }

	  logger.debug("Load all FIX messages for test")
	  val messages: List[(Message, SessionID)] = fixMsgReader.readAll("src/test/resources/data/orders8.fix")
	  logger.debug("FIX messages for test loaded")

	  val appRunner = new AppRunner()
	  appRunner.start()
	  val fixMessageHandler: FixMessageHandler = OrderBookApp.fixMessageHandler
	  while(!appRunner.isAlive) {
		logger.info("Wait for alive appRunner")
		Thread.sleep(1000)
	  }
	  logger.info("AppRunner is alive")

	  logger.info("Apply FIX messages")
	  messages.foreach(m => fixMessageHandler.fromApp(m._1, m._2))
	  logger.info("Applied FIX messages")

	  new AppProgressChecker().execute()

	  OrderBookApp.stop()
	}
  }

  class AppRunner extends Thread("OrderBookApp") {
	override def run(): Unit = OrderBookApp.main(Array())
  }

  class AppProgressChecker {
	val appRoot = ScriptRunner.executeScript("app-root.sh")
	val scriptsPath = "src/test/resources/scripts"
	val logFile = "nexbook.log"

	import sys.process._

	val phraseFMH = "FixMessageHandler - onMessage:"
	val phraseME = "MatchingEngine - Order processed"
	val phraseTDS = "TradeDatabaseSaver - Saved order"

	def execute() = {
	  while (!isAppFinished) {
		val countFMH = countOccurrencesInLogFile(phraseFMH)
		val countME = countOccurrencesInLogFile(phraseME)
		val countTDS = countOccurrencesInLogFile(phraseTDS)

		logger.info(s"Current counts - FMH: $countFMH, ME: $countME, TDS: $countTDS")

		Thread.sleep(5000)
		logger.info(s"Afterwait: current counts")
	  }
	  logger.info("Progress checker finished")
	}

	def isAppFinished: Boolean = {
	  logger.info("executing isAppFinished")
	  def allOrdersProcessedInFixMessageHandler: Boolean = countOccurrencesInLogFile(phraseFMH) >= expectedCount
	  def allOrdersHandlerInMatchingEngine: Boolean = countOccurrencesInLogFile(phraseME) >= expectedCount
	  def allOrdersSavedInDb: Boolean = countOccurrencesInLogFile(phraseTDS) >= expectedCount

	  def appFinishedConditions: List[Boolean] = List(allOrdersProcessedInFixMessageHandler, allOrdersHandlerInMatchingEngine, allOrdersSavedInDb)

	  appFinishedConditions.reduce(_ && _)
	}





	def countOccurrencesInLogFile(phrase: String): Int = {
	  val cmd = s"less $appRoot/$logFile | grep '$phrase' | wc -l"
	  val output = (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	  if (!output.matches("\\d+")) {
		logger.warn(s"returned output: $output for $phrase"); 0
	  } else output.toInt
	}
  }

}
