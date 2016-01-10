package org.nexbook.neworderhandler.pubsub

import java.util.concurrent.Executors

import org.nexbook.app.AppConfig
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.NewOrderHandler


/**
  * Created by milczu on 12/21/15.
  */
class AsyncPubSubNewOrderHandler(delegators: List[NewOrderHandler]) extends PubSubNewOrderHandler(delegators) {
  val executors = AppConfig.supportedCurrencyPairs.map(symbol => symbol -> Executors.newSingleThreadScheduledExecutor()).toMap

  override def handleNewOrder(o: NewOrder): Unit = executors(o.symbol).execute(new Runnable {
	override def run(): Unit = newOrderPublisher.handle(o)
  })

  override def handleNewOrderCancel(c: NewOrderCancel): Unit = executors(c.symbol).execute(new Runnable {
	override def run(): Unit = cancelOrderPublisher.handle(c)
  })




}
