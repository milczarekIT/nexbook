package org.nexbook.performance.repository

import org.nexbook.domain.Order
import org.nexbook.performance.StopWatch
import org.nexbook.repository._
import org.nexbook.tags.PerformanceTest
import org.nexbook.testutils.OrderProvider
import org.scalatest.concurrent.Timeouts
import org.scalatest.{Matchers, WordSpecLike}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode
import scala.util.Random

/**
  * Created by milczu on 03.01.16.
  */
class OrderInMemoryRepositoryTest extends WordSpecLike with Matchers with Timeouts with StopWatch {

  val logger = LoggerFactory.getLogger(classOf[OrderInMemoryRepositoryTest])

  val ordersCounts = Seq(5000, 50000, 100000)

  "OrderInMemoryRepository add operation" should {
	val allOrders = OrderProvider.get(ordersCounts.max)
	for (ordersCount <- ordersCounts) {
	  s"add fast $ordersCount orders" taggedAs PerformanceTest in {
		logger.info(s"====== Performance test for add operation for OrderInMemoryRepository, orders count: $ordersCount")
		val orders = allOrders.take(ordersCount)
		orders should have size ordersCount
		logger.info(s"Loaded $ordersCount orders as test data. Test starting")

		testAddOperation(orders)
	  }
	}
  }

  "OrderInMemoryRepository findByClOrdId operation" should {
	val findFactor = 0.05
	val allOrders = OrderProvider.get(ordersCounts.max)

	for (ordersCount <- ordersCounts) {
	  val orders = allOrders.take(ordersCount)
	  val findCount: Int = (findFactor * orders.size).toInt
	  s"find fast $findCount results for $ordersCount orders in repo" taggedAs PerformanceTest in {
		logger.info(s"====== Performance test for findByClOrdId operation, orders count: $ordersCount. Find factor is: $findFactor, so $findCount find operations will be executed")

		orders should have size ordersCount
		logger.info(s"Loaded $ordersCount orders as test data. Test starting")

		testFindByClOrdIdOperation(orders, findCount)
	  }
	}
  }

  def logFinalResult(higher: Long, lower: Long, higherClass: Class[_]) = {
	val percent = BigDecimal(higher / lower.toDouble * 100 - 100).setScale(2, RoundingMode.HALF_UP).toDouble
	logger.info(s"this operation is $percent% slower in ${higherClass.getName}")
  }

  def testAddOperation(orders: List[Order]): Unit = {
	val attemptsForWarmCpuCache = 2
	val repeats = attemptsForWarmCpuCache + 5

	def measure(repository: OrderInMemoryRepository, orders: List[Order]): Long = {
	  val execTime: Long = stopwatch {
		failAfter(60 seconds) {
		  orders.foreach(repository.add)
		}
	  }
	  repository.count should be(orders.size)
	  logger.trace(s"Exec time: $execTime (${repository.getClass.getName})")
	  execTime
	}

	val avgExecTimeImmutable = (Seq.fill(repeats)(measure(new immutable.OrderInMemoryRepository, orders)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimeImmutable: $avgExecTimeImmutable μs")

	val avgExecTimeMutable = (Seq.fill(repeats)(measure(new mutable.OrderInMemoryRepository, orders)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimeMutable: $avgExecTimeMutable μs")


	if (avgExecTimeImmutable > avgExecTimeMutable) {
	  logFinalResult(avgExecTimeImmutable, avgExecTimeMutable, classOf[immutable.OrderInMemoryRepository])
	} else {
	  logFinalResult(avgExecTimeMutable, avgExecTimeImmutable, classOf[mutable.OrderInMemoryRepository])
	}
  }

  def testFindByClOrdIdOperation(orders: List[Order], findCount: Int): Unit = {
	val attemptsForWarmCpuCache = 2
	val repeats = attemptsForWarmCpuCache + 5

	val clOrdIds: List[String] = Random.shuffle(orders).take(findCount).map(_.clOrdId)

	def measure(repository: OrderInMemoryRepository, orders: List[Order], clOrdIds: List[String]): Long = {
	  orders.foreach(repository.add)
	  repository.count should be(orders.size)
	  stopwatch {
		failAfter(15 seconds) {
		  clOrdIds.foreach(repository.findByClOrdId)
		}
	  }
	}

	val avgExecTimeSeq = (Seq.fill(repeats)(measure(new mutable.OrderInMemoryRepository, orders, clOrdIds)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimeSeq: $avgExecTimeSeq μs")

	val avgExecTimePar = (Seq.fill(repeats)(measure(new mutable.OrderInMemoryParRepository, orders, clOrdIds)).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds.toMicros
	logger.info(s"avgExecTimePar: $avgExecTimePar μs")

	if (avgExecTimeSeq > avgExecTimePar) {
	  logFinalResult(avgExecTimeSeq, avgExecTimePar, classOf[mutable.OrderInMemoryRepository])
	} else {
	  logFinalResult(avgExecTimePar, avgExecTimeSeq, classOf[mutable.OrderInMemoryParRepository])
	}
  }
}
