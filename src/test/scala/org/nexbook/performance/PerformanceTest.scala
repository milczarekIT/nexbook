package org.nexbook.performance

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest.concurrent.Timeouts
import org.scalatest.{Matchers, WordSpecLike}

/**
  * Created by milczu on 07.01.16.
  */
trait PerformanceTest extends WordSpecLike with Matchers with Timeouts {

  val counter = new AtomicInteger

  def asyncExecute(f: => Any): Thread = asyncExecute(s"async-executor-${counter.incrementAndGet}")(f)

  def asyncExecute(threadName: String)(f: => Any): Thread = {
	val thread = new Thread(new Runnable {
	  override def run(): Unit = f
	}, threadName)
	thread.start()
	thread
  }
}
