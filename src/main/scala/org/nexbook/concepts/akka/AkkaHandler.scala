package org.nexbook.concepts.akka

import akka.actor.{ActorRef, ActorSystem}
import org.nexbook.core.Handler
import org.nexbook.utils.AkkaUtils

/**
  * Created by milczu on 12/21/15.
  */
trait AkkaHandler[T] extends Handler[T] {

  def actorSystem: ActorSystem = AkkaUtils.actorSystem

  def actorRefHandlers: List[ActorRef]

  override def handle(o: T) = actorRefHandlers foreach(_ ! o)
}
