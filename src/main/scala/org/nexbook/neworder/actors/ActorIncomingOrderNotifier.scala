package org.nexbook.neworder.actors

import akka.actor.ActorRef
import org.nexbook.domain.NewOrder
import org.nexbook.neworder.IncomingOrderNotifier

/**
 * Created by milczu on 11.12.15
 */
class ActorIncomingOrderNotifier(listener: ActorRef) extends IncomingOrderNotifier {

  override def notify(newOrder: NewOrder): Unit = listener ! newOrder
}
