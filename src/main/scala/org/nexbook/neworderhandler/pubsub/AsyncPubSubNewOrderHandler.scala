package org.nexbook.neworderhandler.pubsub

import java.util.concurrent.Executors

import org.nexbook.app.AppConfig
import org.nexbook.domain.{NewOrder, NewOrderCancel}
import org.nexbook.neworderhandler.NewOrderHandler


/**
  * Created by milczu on 12/21/15.
  */
class AsyncPubSubNewOrderHandler(delegators: List[NewOrderHandler]) extends PubSubNewOrderHandler(delegators) {
  val executor = Executors.newFixedThreadPool(AppConfig.supportedCurrencyPairs.size)

  override def handleNewOrder(o: NewOrder): Unit = executor.execute(new Runnable {
	override def run(): Unit = newOrderPublisher.handle(o)
  })

  override def handleNewOrderCancel(c: NewOrderCancel): Unit = executor.execute(new Runnable {
	override def run(): Unit = cancelOrderPublisher.handle(c)
  })




}
