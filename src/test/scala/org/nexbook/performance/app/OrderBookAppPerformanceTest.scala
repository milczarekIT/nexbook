package org.nexbook.performance.app

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.nexbook.app.OrderBookApp
import org.nexbook.fix.FixMessageHandler
import org.nexbook.tags.{Integration, Performance}
import org.nexbook.testutils.FixMessageProvider
import org.scalatest.concurrent.Timeouts
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import quickfix.field.MsgType
import quickfix.{Message, SessionID}

/**
  * Created by milczu on 1/2/16.
  */
class OrderBookAppPerformanceTest extends FlatSpec with Matchers with Timeouts {

  System.setProperty("config.name", "nexbook")
  val logger = LoggerFactory.getLogger(classOf[OrderBookAppPerformanceTest])
  val testDataPath = "src/test/resources/data/orders8_50k.fix"
  val dbCollections = List("orders", "executions")
  val expectedTotalOrdersCount = 50000//95248 + 4752 // Orders: 95248, Cancels: 4752, total: 100000

  import org.scalatest.time.SpanSugar._

  "testData orders8_2k.fix" should "contains 95248 NewOrderSingle and 4752 OrderCancelRequest" taggedAs Integration in {
	logger.info(s"load test data: $testDataPath")
	val messages: List[Message] = FixMessageProvider.get(testDataPath).map(_._1)

	messages should have size expectedTotalOrdersCount

	val countsByMsgType: Map[String, Int] = messages.groupBy(m => m.getHeader.getField(new MsgType()).getValue).map(e => e._1 -> e._2.size).toMap

//	countsByMsgType("D") should equal(95248)
//	countsByMsgType("F") should equal(4752)

  }

  "OrderBook" should "work fast!" taggedAs Performance in {
	failAfter(600 seconds) {
	  logger.info("Test run!")
	  dbCollections.foreach(MongodbTestUtils.dropCollection)

	  logger.debug("Load all FIX messages for test")
	  val messages: List[(Message, SessionID)] = FixMessageProvider.get(testDataPath)
	  logger.debug("FIX messages for test loaded")

	  new AppRunner().start()
	  val fixMessageHandler: FixMessageHandler = OrderBookApp.fixMessageHandler

	  new Thread(new Runnable {
		override def run(): Unit = {
		  logger.info("Apply FIX messages")
		  messages.foreach(m => fixMessageHandler.fromApp(m._1, m._2))
		  logger.info("Applied FIX messages")
		}
	  }, "Async FIX message applier").start()



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
	val phraseNotApprovedCancel = "MatchingEngine - Unable to cancel order"

	def execute() = {
	  Thread.sleep(5000)
	  while (!isAppFinished) {
		val countTDS = countOccurrencesInLogFile(phraseTDS)
		val countME = countOccurrencesInLogFile(phraseME)
		val countFMH = countOccurrencesInLogFile(phraseFMH)

		logger.info(s"Current counts - FMH: $countFMH, ME: $countME, TDS: $countTDS")

		Thread.sleep(10000)
	  }
	  logger.info("Progress checker finished")
	  val startLine = findFirstOccurrenceInLogFile(phraseFMH)
	  val endLine = findLastOccurrenceInLogFile(phraseME)

	  def extractTimeFromLogFile(line: String) = LocalTime.parse(line.split(" ")(0), DateTimeFormat.forPattern("HH:mm:ss.SSS"))

	  val startTime = extractTimeFromLogFile(startLine)
	  val endTime = extractTimeFromLogFile(endLine)
	  logger.info(s"Start: $startTime")
	  logger.info(s"End: $endTime")
	  val execTime = endTime.toDateTimeToday.getMillis - startTime.toDateTimeToday.getMillis
	  val execTimeInSeconds = execTime / 1000
	  val throughput = expectedTotalOrdersCount / execTimeInSeconds
	  logger.info(s"Exec time: ${execTime}ms. Throughput: $throughput orders/s")

	}

	def isAppFinished: Boolean = {
	  logger.info("executing isAppFinished")
	  def allOrdersProcessedInFixMessageHandler: Boolean = countOccurrencesInLogFile(phraseFMH) >= expectedTotalOrdersCount
	  def allOrdersHandlerInMatchingEngine: Boolean = countOccurrencesInLogFile(phraseME) >= expectedTotalOrdersCount
	  def allOrdersSavedInDb: Boolean = {
		val notApprovedCancel = countOccurrencesInLogFile(phraseNotApprovedCancel)
		countOccurrencesInLogFile(phraseTDS) >= (expectedTotalOrdersCount - notApprovedCancel)
	  }

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

	def findFirstOccurrenceInLogFile(phrase: String): String = {
	  val cmd = s"less $appRoot/$logFile | grep '$phrase' | head -n 1"
	  (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	}

	def findLastOccurrenceInLogFile(phrase: String): String = {
	  val cmd = s"less $appRoot/$logFile | grep '$phrase' | tail -n 1"
	  (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	}
  }

}
