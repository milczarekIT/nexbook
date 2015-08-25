package org.nexbook.orderprocessing

/**
 * Created by milczu on 25.08.15.
 */
trait OrderProcessingResponseLifecycleFactory {

  def sender: OrderProcessingResponseSender

  def handler: OrderProcessingResponseHandler
}
