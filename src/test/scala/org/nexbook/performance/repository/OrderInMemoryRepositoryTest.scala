package org.nexbook.performance.repository

import org.nexbook.domain.Order
import org.nexbook.repository._
import org.nexbook.tags.PerformanceTest
import org.nexbook.testutils.OrderProvider
import org.scalatest.{Matchers, WordSpecLike}
import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode

/**
  * Created by milczu on 03.01.16.
  */
class OrderInMemoryRepositoryTest extends WordSpecLike with Matchers {

  val logger = LoggerFactory.getLogger(classOf[OrderInMemoryRepositoryTest])

  "OrderInMemoryRepository add operation" should {
	val ordersCounts = Seq(5000, 50000, 100000)
	val allOrders = OrderProvider.get(ordersCounts.max)
	for (ordersCount <- ordersCounts) {
	  s"work fast for $ordersCount orders" taggedAs PerformanceTest in {
		logger.info(s"====== Performance test for add operation for OrderInMemoryRepository, orders count: $ordersCount")
		val orders = allOrders.take(ordersCount)
		orders should have size ordersCount
		logger.info(s"Loaded $ordersCount orders as test data. Test starting")

		testAddOperation(orders)
	  }
	}
  }

  def testAddOperation(orders: List[Order]): Unit = {
	val attemptsForWarmCpuCache = 2
	val repeats = attemptsForWarmCpuCache + 5

	import scala.concurrent.duration._

	def measure(repository: OrderInMemoryRepository, orders: List[Order]): Long = {
	  val start = System.nanoTime
	  orders.foreach(repository.add)
	  val end = System.nanoTime
	  repository.count should be (orders.size)
	  val execTime = end - start
	  logger.trace(s"Exec time: $execTime (${repository.getClass.getName})")
	  execTime
	}

	val avgExecTimeImmutable = (Seq.fill(repeats)(measure(new immutable.OrderInMemoryRepository, orders)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimeImmutable: $avgExecTimeImmutable μs")

	val avgExecTimeMutable = (Seq.fill(repeats)(measure(new mutable.OrderInMemoryRepository, orders)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimeMutable: $avgExecTimeMutable μs")

	def logFinalResult(higher: Long, lower: Long, higherClass: Class[_]) = {
	  val percent = BigDecimal(higher / lower.toDouble * 100 - 100).setScale(2, RoundingMode.HALF_UP).toDouble
	  logger.info(s"add operation in ${higherClass.getName} is $percent% slower")
	}

	if(avgExecTimeImmutable > avgExecTimeMutable) {
	  logFinalResult(avgExecTimeImmutable, avgExecTimeMutable, classOf[immutable.OrderInMemoryRepository])
	} else {
	  logFinalResult(avgExecTimeMutable, avgExecTimeImmutable, classOf[mutable.OrderInMemoryRepository])
	}
  }
}
