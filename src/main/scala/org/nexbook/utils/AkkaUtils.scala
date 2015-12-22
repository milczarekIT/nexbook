package org.nexbook.utils

import akka.actor.ActorSystem

/**
  * Created by milczu on 12/21/15.
  */
object AkkaUtils {
	lazy val actorSystem = ActorSystem("OrderBookSystem")
}
