package org.nexbook.performance

/**
  * Created by milczu on 04.01.16.
  */
trait StopWatch {

  def stopwatch(f: => Any): Long = {
  	val start = System.currentTimeMillis
  	f
  	val end = System.currentTimeMillis
	end - start
  }
}
