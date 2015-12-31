package org.nexbook.orderchange

import org.nexbook.core.Handler
import org.nexbook.repository.OrderDatabaseRepository
import org.slf4j.LoggerFactory

import scala.util.Try

/**
  * Created by milczu on 12/23/15.
  */
class DbUpdateOrderChangeHandler(orderDatabaseRepository: OrderDatabaseRepository) extends Handler[OrderChangeCommand] {

  val logger = LoggerFactory.getLogger(classOf[DbUpdateOrderChangeHandler])

  type updateOnDb = OrderChange => Boolean

  val updateDelayMillis: Int = 1000
  val attemptsLimit = 20

  val statusChangeUpdate: updateOnDb = orderChange => onOrderStatusChange(orderChange.asInstanceOf[OrderStatusChange])
  val fillChangeUpdate: updateOnDb = orderChange => onOrderFillChange(orderChange.asInstanceOf[OrderFillChange])


  override def handle(changeCommand: OrderChangeCommand) = changeCommand.payload match {
	case fc: OrderFillChange => tryUpdate(fc, fillChangeUpdate)
	case sc: OrderStatusChange => tryUpdate(sc, statusChangeUpdate)
  }

  def onOrderFillChange(fillChange: OrderFillChange):Boolean = {
	true
  }

  def onOrderStatusChange(statusChange: OrderStatusChange): Boolean = {
	orderDatabaseRepository.updateStatus(statusChange.tradeID, statusChange.status, statusChange.prevStatus)
  }

  def tryUpdate(orderChange: OrderChange, updateFunc: updateOnDb): Unit = {
	import scala.concurrent._
	import ExecutionContext.Implicits.global
	import scala.concurrent.duration._
	def delay = Try(Await.ready(Promise().future, updateDelayMillis.milliseconds.fromNow.timeLeft))

	var res = updateFunc(orderChange)
	if(res) {
	  logger.debug(s"Update $orderChange was applied without delay")
	}
	var attempt = 0
	while(!res && attempt < attemptsLimit) {
	  	logger.info(s"Order change $orderChange not applied. Next async attempt ${attempt+1} will be applied in next ${updateDelayMillis}ms")
		val f: Future[Boolean] = Future{delay; updateFunc(orderChange)}
	  	res = Await.result(f, Duration.Inf)
	  	attempt = attempt + 1
	}
	if(!res && attempt >= attemptsLimit) {
		logger.warn(s"OrderChange: $orderChange was not applied after")
	}
  }
}
