package org.nexbook.concepts.akka

import akka.actor.Actor
import org.nexbook.core.Handler

/**
  * Created by milczu on 12/21/15.
  */
class AkkaHandlerWrapper[T](handler: Handler[T]) extends Actor {
  override def receive: Receive = {
	case o: T => handler handle o
  }
}
