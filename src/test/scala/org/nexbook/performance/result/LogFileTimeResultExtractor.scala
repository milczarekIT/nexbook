package org.nexbook.performance.result

import scala.io.Source
import scala.util.matching.Regex

/**
  * Created by milczu on 07.01.16.
  */
object LogFileTimeResultExtractor {

  type NanoTime = Long
  type ClOrdId = String

  val nanoTimeGroupIndex = 1
  val clOrdIdGroupIndex = 4


  def extractTimes(filePath: String, className: String): Map[ClOrdId, NanoTime] = {
    val regex: Regex = s"^\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s(\\d*)\\s\\[.*\\]\\s(TRACE|DEBUG|INFO).*($className)\\s\\-\\s([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}).*$$".r
	def matchingLine(line: String): Boolean = regex.findAllMatchIn(line).nonEmpty
	def extractResultFromLine(line: String): (ClOrdId, NanoTime) = {
	  regex.findAllIn(line).matchData.toList.map(m => (m.group(clOrdIdGroupIndex), m.group(nanoTimeGroupIndex).toLong)).head
	}

	Source.fromFile(filePath).getLines().filter(matchingLine).map(extractResultFromLine).toMap
  }





}
