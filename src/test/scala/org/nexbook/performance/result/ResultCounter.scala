package org.nexbook.performance.result

import org.nexbook.performance.result.LogFileExecTimeResolver.Microseconds
import org.nexbook.utils.Assert

/**
  * Created by milczu on 07.01.16.
  */
object ResultCounter {

  def count(pathToFile: String, expectedResultSize: Int): Result = {
	val execTimesResult: (Long, Int, List[Microseconds]) = LogFileExecTimeResolver.resolveExecTimes(pathToFile, expectedResultSize)
	val totalExecTimeMs = execTimesResult._1
	val throughput: Int = execTimesResult._2
	val execTimesMicroseconds: List[Microseconds] = execTimesResult._3.sorted

	def percentiles: List[Microseconds] = {
	  val listSize = execTimesMicroseconds.size
	  def percentile(percValue: Int):Microseconds = {
		val index: Int =  (listSize * percValue / 100.00).toInt
		execTimesMicroseconds(index)
	  }
	  List(percentile(5), percentile(25), percentile(50), percentile(75), percentile(95))
	}


	val meanExecTimes = (execTimesMicroseconds.sum / execTimesMicroseconds.size.toDouble).toLong
	new Result(expectedResultSize, totalExecTimeMs, throughput, meanExecTimes, percentiles)
  }
}

class Result(val size: Int, val totalExecTimeMs: Long, val throughput: Int, val mean: Microseconds, percentiles: List[Microseconds]) {

  Assert.isTrue(percentiles.size == 5)

  val percentile5: Microseconds = percentiles(0)
  val percentile25: Microseconds = percentiles(1)
  val percentile50: Microseconds = percentiles(2)
  val percentile75: Microseconds = percentiles(3)
  val percentile95: Microseconds = percentiles(4)
}
