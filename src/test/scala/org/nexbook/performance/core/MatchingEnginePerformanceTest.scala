package org.nexbook.performance.core

import org.nexbook.core.{MatchingEngine, OrderBook}
import org.nexbook.performance.StopWatch
import org.nexbook.repository.mutable.OrderInMemoryRepository
import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository}
import org.nexbook.sequence.SequencerFactory
import org.nexbook.tags.Performance
import org.nexbook.testutils.OrderProvider
import org.scalatest.concurrent.Timeouts
import org.scalatest.mock.MockitoSugar._
import org.scalatest.{Matchers, WordSpecLike}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
  * Created by milczu on 05.01.16.
  */
class MatchingEnginePerformanceTest extends WordSpecLike with Matchers with Timeouts with StopWatch {

  val logger = LoggerFactory.getLogger(classOf[MatchingEnginePerformanceTest])
  val orders = OrderProvider.get(50000)//.filter(_.symbol == "EUR/USD").take(12000)

  "OrderInMemoryRepository add operation" should {
	"do sth" taggedAs Performance in {
	  val attemptsForWarmCpuCache = 2
	  val repeats = attemptsForWarmCpuCache + 10

	  def measure(matchingEngine: MatchingEngine) = stopwatch { orders.foreach(matchingEngine.processOrder) }

	  val ordersCount = orders.size
	  val avgExecTimeNs = (Seq.fill(repeats)(measure(matchingEngine)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toNanos
	  val avgSingleExecTimeMicroseconds = (avgExecTimeNs / ordersCount).nanoseconds.toMicros
	  val throughput = ordersCount * 1.second.toNanos / avgExecTimeNs
	  logger.info(s"avgExecTimeImmutable: ${avgExecTimeNs.nanoseconds.toMillis}ms, latency: ${avgSingleExecTimeMicroseconds}μs, TPS: $throughput")
	}
  }

  def matchingEngine = new MatchingEngine(new OrderInMemoryRepository, new SequencerFactory(mock[OrderDatabaseRepository], mock[ExecutionDatabaseRepository]), new OrderBook, List(), List())

}
