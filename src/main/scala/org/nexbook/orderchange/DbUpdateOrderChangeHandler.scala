package org.nexbook.orderchange

import org.nexbook.core.Handler
import org.nexbook.repository.OrderDatabaseRepository
import org.slf4j.LoggerFactory

import scala.util.Try

/**
  * Created by milczu on 12/23/15.
  */
class DbUpdateOrderChangeHandler(orderDatabaseRepository: OrderDatabaseRepository) extends Handler[OrderChangeCommand] {

  type updateOnDb = OrderChange => Boolean
  val logger = LoggerFactory.getLogger(classOf[DbUpdateOrderChangeHandler])
  val updateDelayMillis = 2000
  val attemptsLimit = 20

  val statusChangeUpdate: updateOnDb = orderChange => onOrderStatusChange(orderChange.asInstanceOf[OrderStatusChange])
  val fillChangeUpdate: updateOnDb = orderChange => onOrderFillChange(orderChange.asInstanceOf[OrderFillChange])


  override def handle(changeCommand: OrderChangeCommand) = changeCommand.payload match {
	case fc: OrderFillChange => tryUpdate(fc, fillChangeUpdate)
	case sc: OrderStatusChange => tryUpdate(sc, statusChangeUpdate)
  }

  def tryUpdate(orderChange: OrderChange, updateFunc: updateOnDb): Unit = {
	logger.trace(s"Db order change: $orderChange")
	var res = updateFunc(orderChange)
	if (res) {
	  logger.debug(s"Update $orderChange was applied without delay")
	} else {
	  import scala.concurrent._
	  import ExecutionContext.Implicits.global
	  import scala.concurrent.duration._

	  var attempt = 0
	  def delay = Try(Await.ready(Promise().future, updateDelayMillis.milliseconds.fromNow.timeLeft))
	  while (!res && attempt < attemptsLimit) {
		logger.debug(s"Order change $orderChange not applied. Next async attempt ${attempt + 1} will be applied in next ${updateDelayMillis}ms")
		val f: Future[Boolean] = Future {
		  delay; updateFunc(orderChange)
		}
		res = Await.result(f, Duration.Inf)
		attempt = attempt + 1
	  }
	  if (!res && attempt >= attemptsLimit) {
		logger.warn(s"OrderChange: $orderChange was not applied after")
	  }
	}
  }

  def onOrderFillChange(fillChange: OrderFillChange): Boolean = {
	orderDatabaseRepository.updateStatusAndLeaveQty(fillChange.tradeID, fillChange.status, fillChange.leaveQty, fillChange.prevStatus, fillChange.prevLeaveQty)
  }

  def onOrderStatusChange(statusChange: OrderStatusChange): Boolean = {
	orderDatabaseRepository.updateStatus(statusChange.tradeID, statusChange.status, statusChange.prevStatus)
  }
}
