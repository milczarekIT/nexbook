package org.nexbook.performance

import org.nexbook.domain.Order
import org.nexbook.tags.Performance
import org.nexbook.testutils.OrderProvider
import org.scalatest.{Matchers, WordSpecLike}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * Created by milczu on 04.01.16.
  */
class VolumeSumTest extends WordSpecLike with Matchers with StopWatch {

  val logger = LoggerFactory.getLogger(classOf[VolumeSumTest])

  val ordersCounts = Seq(5000, 50000, 100000)

  "Seq and Par Set processing" should {
	val allOrders = OrderProvider.get(ordersCounts.max)
	for (ordersCount <- ordersCounts) {
	  s"sum volume $ordersCount orders" taggedAs Performance in {
		logger.info(s"====== Performance test for add operation for OrderInMemoryRepository, orders count: $ordersCount")
		val orders = allOrders.take(ordersCount)
		orders should have size ordersCount
		logger.info(s"Loaded $ordersCount orders as test data. Test starting")

		testCountOperation(orders)
	  }
	}
  }

  def testCountOperation(orders: List[Order]) {
	val attemptsForWarmCpuCache = 2
	val repeats = attemptsForWarmCpuCache + 15

	val ordersSet: mutable.SortedSet[Order] = mutable.TreeSet.empty[Order](Ordering.fromLessThan(_.tradeID < _.tradeID))
	orders.foreach(ordersSet.+=)
	ordersSet should have size orders.size

	def measureSeq: Long = stopwatch {
	  ordersSet.foldLeft(0.00)(_ + _.leaveQty)
	}

	def measurePar: Long = stopwatch {
	  ordersSet.par.foldLeft(0.00)(_ + _.leaveQty)
	}

	val avgExecTimeSeq = (Seq.fill(repeats)(measureSeq).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds
	val avgExecTimePar = (Seq.fill(repeats)(measurePar).drop(attemptsForWarmCpuCache).sum / repeats).nanoseconds
	logger.info(s"avgExecTimeSeq: ${avgExecTimeSeq}ns")
	logger.info(s"avgExecTimePar: ${avgExecTimePar}ns")
  }

}
