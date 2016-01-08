package org.nexbook.performance.app

import com.typesafe.config.Config
import org.nexbook.app.{AppConfig, OrderBookApp}
import org.nexbook.fix.FixMessageHandler
import org.nexbook.performance.PerformanceTest
import org.nexbook.performance.result.ResultLogger
import org.nexbook.testutils.FixMessageProvider
import org.slf4j.Logger
import quickfix.{Message, SessionID}

/**
  * Created by milczu on 08.01.16.
  */
trait OrderBookAppPerformanceTest extends PerformanceTest {

  val appRoot = ScriptRunner.executeScript("app-root.sh")
  val resultLogger = new ResultLogger
  val dbCollections = List("orders", "executions")

  def cleanBeforeTest(): Unit = dbCollections.foreach(MongodbTestUtils.dropCollection)

  def logger: Logger

  def benchmarkConfig: Config

  def testDataPath: String

  def resultLog: String

  def expectedTotalOrdersCount: Int

  val fixMessageApplierThreadPool = 4

  def executeTest() = {
	logger.info("Test run!")
	logger.debug("Load all FIX messages for test")
	val messages: List[(Message, SessionID)] = FixMessageProvider.get(testDataPath)
	logger.debug("FIX messages for test loaded")

	cleanBeforeTest()

	asyncExecute("OrderBookApp") {
	  OrderBookApp.main(Array())
	}
	applyFixMessages(messages, OrderBookApp.fixMessageHandler)

	new AppProgressChecker().execute()

	resultLogger.logResultToFile(benchmarkConfig, resultLog, writeHeader = true)
	OrderBookApp.stop()
  }


  def applyFixMessages(messages: List[(Message, SessionID)], fixMessageHandler: FixMessageHandler) = {
	if (fixMessageApplierThreadPool == 1) {
	  asyncExecute("Async FIX message applier") {
		logger.info("Apply FIX messages")
		messages.foreach(m => fixMessageHandler.fromApp(m._1, m._2))
		logger.info("Applied FIX messages")
	  }
	} else {
	  val partitionSize = math.ceil(messages.size / fixMessageApplierThreadPool.toDouble).toInt
	  val messagesPartitions: List[List[(Message, SessionID)]] = messages.grouped(partitionSize).toList
	  for (part <- messagesPartitions.indices) {
		asyncExecute(s"Async FIX message applier: $part") {
		  logger.info("Apply FIX messages")
		  messagesPartitions(part).foreach(m => fixMessageHandler.fromApp(m._1, m._2))
		  logger.info(s"Applied FIX messages: $part")
		}
	  }
	}
  }

  class AppProgressChecker {
	val scriptsPath = "src/test/resources/scripts"
	val logFile = "nexbook.log"

	import sys.process._

	val phraseFMH = "FixMessageHandler - .* - onMessage:"
	val phraseME = "MatchingEngine - .* - Order processed"
	val phraseTDS = "TradeDatabaseSaver - .* - Saved order"

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
	  val cmd = s"cp $appRoot/logs/$logFile $appRoot/logs/temp.log && less $appRoot/logs/temp.log | grep '$phrase' | wc -l"
	  val output = (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	  (stringSeqToProcess(Seq("bash", "-c", s"rm -rf $appRoot/logs/temp.log")) !)
	  if (!output.matches("\\d+")) {
		logger.warn(s"returned output: $output for $phrase")
		0
	  } else output.toInt
	}

	def findFirstOccurrenceInLogFile(phrase: String): String = {
	  val cmd = s"less $appRoot/logs/$logFile | grep '$phrase' | head -n 1"
	  (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	}

	def findLastOccurrenceInLogFile(phrase: String): String = {
	  val cmd = s"less $appRoot/logs/$logFile | grep '$phrase' | tail -n 1"
	  (stringSeqToProcess(Seq("bash", "-c", cmd)) !!).trim
	}
  }
}
