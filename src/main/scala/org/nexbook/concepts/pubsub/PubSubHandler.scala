package org.nexbook.concepts.pubsub

import org.nexbook.core.Handler

import scala.collection.mutable

/**
  * Created by milczu on 12/21/15.
  */
trait PubSubHandler[T] extends Handler[T] with mutable.Publisher[T] {

  def subscribers: List[mutable.Subscriber[T, mutable.Publisher[T]]]

  def init = {
	subscribers.foreach(subscribe)
  }

  override def handle(o: T): Unit = publish(o)

}
