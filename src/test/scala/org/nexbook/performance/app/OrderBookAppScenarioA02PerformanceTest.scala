package org.nexbook.performance.app

import com.typesafe.config.ConfigFactory
import org.nexbook.tags.Performance
import org.slf4j.LoggerFactory


/**
  * Created by milczu on 1/2/16.
  */
class OrderBookAppScenarioA02PerformanceTest extends OrderBookAppPerformanceTest {

  val logger = LoggerFactory.getLogger(classOf[OrderBookAppScenarioA02PerformanceTest])
  val scenarioName = "scenario_A_02"
  System.setProperty("config.name", s"scenarios/$scenarioName")
  val config = ConfigFactory.load(s"config/scenarios/$scenarioName").withFallback(ConfigFactory.load("config/general"))

  override val benchmarkConfig = config.getConfig("benchmark")
  override val testDataPath = s"src/test/resources/data/${benchmarkConfig.getString("testDataFile")}"
  override val resultLog = s"$appRoot/logs/test/$scenarioName.log"
  override val expectedTotalOrdersCount = benchmarkConfig.getInt("expectedOrderCount")

  import org.scalatest.time.SpanSugar._

  s"OrderBook: $scenarioName" should {
	"work fast!" taggedAs Performance in {
	  failAfter(600 seconds) {
		executeTest()
	  }
	}
  }
}
