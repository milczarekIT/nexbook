package org.nexbook.concepts.pubsub

import java.util.concurrent.Executors

import org.nexbook.core.Handler

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
class AsyncPubSubHandlerWrapper[T](handler: Handler[T]) extends mutable.Subscriber[T, mutable.Publisher[T]] {
  val executor = Executors.newSingleThreadExecutor()
  override def notify(pub: mutable.Publisher[T], event: T): Unit = executor.execute(new Runnable {
	override def run(): Unit = handler handle event
  })
}
