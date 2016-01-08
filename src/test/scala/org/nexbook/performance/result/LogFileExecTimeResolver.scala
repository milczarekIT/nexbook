package org.nexbook.performance.result

import org.nexbook.performance.result.LogFileTimeResultExtractor.{ClOrdId, NanoTime}
import org.nexbook.utils.Assert

import scala.concurrent.duration._

/**
  * Created by milczu on 07.01.16.
  */
object LogFileExecTimeResolver {

  type Microseconds = Long

  def resolveExecTimes(pathToFile: String, expectedResultSize: Int): (Long, Int, List[Microseconds]) = {
	def nanoSecondsToMicroSeconds(nanoTime: NanoTime): Microseconds = Duration(nanoTime, NANOSECONDS).toMicros
	def nanoSecondsToMicroMillis(micros: NanoTime) = Duration(micros, NANOSECONDS).toMillis
	def nanoSecondsToMicroMicros(micros: Microseconds) = Duration(micros, NANOSECONDS).toMicros

	//OrderHandler
	val fmhTimes: Map[ClOrdId, NanoTime] = LogFileTimeResultExtractor.extractTimes(pathToFile, "FixMessageHandler")
	val meTimes: Map[ClOrdId, NanoTime] = LogFileTimeResultExtractor.extractTimes(pathToFile, "MatchingEngine")
	Assert.isTrue(fmhTimes.size == expectedResultSize)
	Assert.isTrue(meTimes.size == expectedResultSize)
	val execTimeNanos = meTimes.values.max - fmhTimes.values.min

	val execTimes: List[Microseconds] = fmhTimes.map(e => e._1 -> (meTimes(e._1) - e._2)).values.map(nanoSecondsToMicroSeconds).toList
	val totalExecTimeMs: Long = nanoSecondsToMicroMillis(execTimeNanos)
	Assert.isTrue(execTimes.size == expectedResultSize)
	val throughput : Int= (expectedResultSize * Duration(1, SECONDS).toMicros / nanoSecondsToMicroMicros(execTimeNanos).toDouble).toInt

	(totalExecTimeMs, throughput, execTimes)
  }

}
