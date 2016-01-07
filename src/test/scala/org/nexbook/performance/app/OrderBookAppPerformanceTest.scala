package org.nexbook.performance.app

import org.nexbook.app.{AppConfig, OrderBookApp}
import org.nexbook.fix.FixMessageHandler
import org.nexbook.performance.PerformanceTest
import org.nexbook.tags.Performance
import org.nexbook.testutils.FixMessageProvider
import org.slf4j.LoggerFactory
import quickfix.{Message, SessionID}


/**
  * Created by milczu on 1/2/16.
  */
class OrderBookAppPerformanceTest extends PerformanceTest {

  System.setProperty("config.name", "nexbook")
  val logger = LoggerFactory.getLogger(classOf[OrderBookAppPerformanceTest])
  val testDataPath = "src/test/resources/data/orders8_100k.txt"
  val dbCollections = List("orders", "executions")
  val expectedTotalOrdersCount = 100000

  import org.scalatest.time.SpanSugar._

  "OrderBook" should  {
	"work fast!" taggedAs Performance in {
	  failAfter(300 seconds) {
		logger.info("Test run!")
		dbCollections.foreach(MongodbTestUtils.dropCollection)

		logger.debug("Load all FIX messages for test")
		val messages: List[(Message, SessionID)] = FixMessageProvider.get(testDataPath)
		logger.debug("FIX messages for test loaded")

		asyncExecute("OrderBookApp") { OrderBookApp.main(Array()) }
		val fixMessageHandler: FixMessageHandler = OrderBookApp.fixMessageHandler

		asyncExecute("Async FIX message applier") {
			logger.info("Apply FIX messages")
			messages.foreach(m => fixMessageHandler.fromApp(m._1, m._2))
			logger.info("Applied FIX messages")
		}

		new AppProgressChecker().execute()

		OrderBookApp.stop()
	  }
	}
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
	  Thread.sleep(20000)
	  while (!isAppFinished) {
		val countTDS = MongodbTestUtils.count("orders")
		val countME = countOccurrencesInLogFile(phraseME)
		val countFMH = countOccurrencesInLogFile(phraseFMH)

		logger.info(s"Current counts - FMH: $countFMH, ME: $countME, TDS: $countTDS")

		Thread.sleep(10000)
	  }
	  logger.info("Progress checker finished")
	  val startLine = findFirstOccurrenceInLogFile(phraseFMH)
	  val endLine = findLastOccurrenceInLogFile(phraseME)

	  def extractNanoTimeFromLogFile(line: String): Long = line.split(" ")(1).toLong

	  val startTime = extractNanoTimeFromLogFile(startLine)
	  val endTime = extractNanoTimeFromLogFile(endLine)
	  import scala.concurrent.duration._
	  val execTime = Duration(endTime - startTime, NANOSECONDS)
	  val throughput = (expectedTotalOrdersCount / execTime.toMicros.toDouble * Duration(1, SECONDS).toMicros).toInt
	  logger.info(s"Duration: ${execTime.toMillis}ms. Throughput: $throughput orders/s")

	}

	def isAppFinished: Boolean = {
	  logger.info("executing isAppFinished")
	  def allOrdersProcessedInFixMessageHandler: Boolean = countOccurrencesInLogFile(phraseFMH) == expectedTotalOrdersCount
	  def allOrdersHandlerInMatchingEngine: Boolean = countOccurrencesInLogFile(phraseME) == expectedTotalOrdersCount
	  def allOrdersSavedInDb: Boolean = !AppConfig.dbPersist || MongodbTestUtils.count("orders") == expectedTotalOrdersCount

	  def appFinishedConditions: List[Boolean] = List(allOrdersProcessedInFixMessageHandler, allOrdersHandlerInMatchingEngine, allOrdersSavedInDb) // List(allOrdersProcessedInFixMessageHandler, allOrdersHandlerInMatchingEngine, allOrdersSavedInDb)

	  appFinishedConditions.reduce(_ && _)
	}

	def countOccurrencesInLogFile(phrase: String): Int = {
	  val cmd = s"cp $appRoot/$logFile $appRoot/temp.log && less $appRoot/temp.log | grep '$phrase' | wc -l"
	  val output = (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	  (stringSeqToProcess(Seq("bash", "-c", s"rm -rf $appRoot/temp.log")) !)
	  if (!output.matches("\\d+")) {
		logger.warn(s"returned output: $output for $phrase")
		0
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
