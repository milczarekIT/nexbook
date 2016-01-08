package org.nexbook.performance.result

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

import com.typesafe.config.Config
import org.joda.time.{LocalDate, LocalTime}
import org.nexbook.performance.app.ScriptRunner

import scala.collection.JavaConverters._
import scala.collection.immutable.{SortedMap, TreeMap}

/**
  * Created by milczu on 07.01.16.
  */
class ResultLogger {

  val appRoot = ScriptRunner.executeScript("app-root.sh")
  val logFile = s"$appRoot/logs/nexbook.log"

  def logResultToFile(benchmarkConfig: Config, resultFile: String, writeHeader: Boolean): Unit = {
	val expectedResultSize: Int = benchmarkConfig.getInt("expectedOrderCount")
	val scenarioName = benchmarkConfig.getString("name")
	val description = benchmarkConfig.getString("description") + s" (executed: ${LocalDate.now} ${LocalTime.now})"

	val r: Result = ResultCounter.count(logFile, expectedResultSize)
	val header: List[String] = List("Scenario", "total exec time (ms)", "throughput", "mean (μs)", "5% (μs)", "25% (μs)", "50% (μs)", "75% (μs)", "95% (μs)")
	val data: List[String] = List(scenarioName, r.totalExecTimeMs, r.throughput, r.mean, r.percentile5, r.percentile25, r.percentile50, r.percentile75, r.percentile95).map(_.toString)

	val lines: List[List[String]] = if (writeHeader) List(header, data) else List(data)
	var tabbed: List[String] = addPads(lines).map(_.mkString("\t"))
	if (writeHeader) {
	  tabbed = description :: tabbed
	}

	Files.write(Paths.get(resultFile), tabbed.asJava, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
  }

  private def addPads(lines: List[List[String]]): List[List[String]] = {
	def maxLengths(lists: List[List[String]]): List[Int] = {
	  def lengthsWithIndexes(list: List[String]): SortedMap[Int, Int] = TreeMap(list.zipWithIndex.map(e => e._2 -> e._1.length).toMap.toArray: _*)
	  val lengths: List[SortedMap[Int, Int]] = lists.map(lengthsWithIndexes)
	  (for (index <- lists.head.indices) yield lengths.map(m => m(index)).max).toList
	}
	def padLength(len: Int): Int = {
	  val tabLength = 8
	  if (len % tabLength > tabLength - 3) (math.ceil(len / tabLength.toDouble).toInt + 1) * tabLength
	  else len
	}

	def pad(obj: String, length: Int) = obj.toString.padTo(length, " ").mkString
	val pads: List[Int] = maxLengths(lines).map(padLength)
	def padded(line: List[String]): List[String] = {
	  (for (i <- line.indices) yield pad(line(i), pads(i))).toList
	}
	lines.map(padded)
  }


}
