package org.nexbook.utils

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by milczu on 12/21/15.
  */
object AkkaUtils {
  lazy val actorSystem = ActorSystem("OrderBookSystem", ConfigFactory.load("config/nexbook"))
}
