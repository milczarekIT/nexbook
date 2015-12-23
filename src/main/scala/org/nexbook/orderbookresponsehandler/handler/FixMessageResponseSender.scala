package org.nexbook.orderbookresponsehandler.handler

import org.nexbook.app.AppConfig
import org.nexbook.domain._
import org.nexbook.orderbookresponsehandler.response._
import org.nexbook.utils.FixUtils
import org.slf4j.LoggerFactory
import quickfix.field.{Side, _}
import quickfix.fix44.component.Instrument
import quickfix.fix44.{BusinessMessageReject, ExecutionReport, Message, NewOrderSingle}
import quickfix.{Session, SessionID, SessionSettings}

import scala.collection.JavaConverters._

/**
  * Created by milczu on 06.12.15.
  */
class FixMessageResponseSender extends OrderBookResponseHandler {

  val logger = LoggerFactory.getLogger(classOf[FixMessageResponseSender])
  val fixSessionSettings = new SessionSettings(AppConfig.fixConfigPath)

  val defaultConverter = new DefaultProcessingResponseFixMessageConverter
  val converters: Map[Class[_], ProcessingResponseFixMessageConverter[_]] = Map(
	classOf[OrderAcceptResponse] -> new OrderAcceptResponseFixMessageConverter,
	classOf[OrderExecutionResponse] -> new ExecutionResponseFixMessageConverter,
	classOf[OrderRejectionResponse] -> new RejectionResponseFixMessageConverter,
	classOf[OrderValidationRejectionResponse] -> new OrderValidationRejectionResponseFixMessageConverter
  )

  override def handle(response: OrderBookResponse) = {
	fixMessageResponse(response) match {
	  case Some(m) => Session.sendToTarget(m._1, m._2)
	  case None => logger.error(s"FixMessageConverter not defined for: ${response.getClass}")
	}
  }

  def fixMessageResponse(response: OrderBookResponse): Option[(Message, SessionID)] = converters.getOrElse(response.getClass, defaultConverter).convert(response)

  trait ProcessingResponseFixMessageConverter[T <: OrderBookResponse] {
	def sessionIDByTargetCompID(connector: String): SessionID = fixSessionSettings.sectionIterator().asScala.find(_.getTargetCompID == connector).get

	def convert(response: OrderBookResponse): Option[(Message, SessionID)] = doConvert(response.asInstanceOf[T])

	protected def doConvert(response: T): Option[(Message, SessionID)]
  }

  class OrderAcceptResponseFixMessageConverter extends ProcessingResponseFixMessageConverter[OrderAcceptResponse] {
	override protected def doConvert(response: OrderAcceptResponse): Option[(Message, SessionID)] = {
	  def toExecutionReport(order: Order): ExecutionReport = {
		def convertFixFields: (ExecType, OrdStatus, Side, OrdType) = {
		  val side = FixUtils.side(order.side)
		  val ordType = FixUtils.ordType(order.orderType)
		  val ordStatus: OrdStatus = if (order.status == Cancelled) new OrdStatus(OrdStatus.CANCELED) else new OrdStatus(OrdStatus.PENDING_NEW)
		  val execType = new ExecType(ExecType.TRADE)
		  (execType, ordStatus, side, ordType)
		}
		val (execType, ordStatus, side, ordType) = convertFixFields
		val executionReport = new ExecutionReport(new OrderID(order.dealID.toString), new ExecID(order.tradeID.toString), execType, ordStatus, side, new LeavesQty(order.leaveQty), new CumQty(order.qty - order.leaveQty), new AvgPx(0.00))

		executionReport.set(new TransactTime(order.timestamp.toDate))
		executionReport.set(new Account(order.clientId))
		executionReport.set(new ClOrdID(order.clOrdId))
		executionReport.set(ordType)
		executionReport.set(new Instrument(new Symbol(order.symbol)))

		executionReport
	  }
	  Some(toExecutionReport(response.order), sessionIDByTargetCompID(response.order.connector))
	}
  }

  class DefaultProcessingResponseFixMessageConverter extends ProcessingResponseFixMessageConverter[OrderBookResponse] {
	override protected def doConvert(response: OrderBookResponse): Option[(Message, SessionID)] = None
  }

  class ExecutionResponseFixMessageConverter extends ProcessingResponseFixMessageConverter[OrderExecutionResponse] {
	override protected def doConvert(response: OrderExecutionResponse): Option[(ExecutionReport, SessionID)] = {
	  def toExecutionReport(execution: OrderExecution): ExecutionReport = {
		def convertFixFields: (ExecType, OrdStatus, Side, OrdType) = {
		  val side = FixUtils.side(execution.side)
		  val ordType = FixUtils.ordType(execution.orderType)
		  val (execType, ordStatus) = if (execution.leaveQty == 0.00) (new ExecType(ExecType.TRADE), new OrdStatus(OrdStatus.FILLED)) else (new ExecType(ExecType.TRADE), new OrdStatus(OrdStatus.PARTIALLY_FILLED))
		  (execType, ordStatus, side, ordType)
		}
		val (execType, ordStatus, side, ordType) = convertFixFields
		val executionReport = new ExecutionReport(new OrderID(execution.dealID.toString), new ExecID(execution.tradeID.toString), execType, ordStatus, side, new LeavesQty(execution.leaveQty), new CumQty(execution.qty - execution.leaveQty), new AvgPx(execution.executionPrice))

		executionReport.set(new LastPx(execution.executionPrice))
		executionReport.set(new LastQty(execution.executionQty))
		executionReport.set(new TransactTime(execution.timestamp.toDate))
		executionReport.set(new Account(execution.clientId))
		executionReport.set(new ClOrdID(execution.clOrdId))
		executionReport.set(ordType)
		executionReport.set(new Instrument(new Symbol(execution.symbol)))

		executionReport
	  }
	  Some(toExecutionReport(response.orderExecution), sessionIDByTargetCompID(response.orderExecution.connector))
	}
  }

  class RejectionResponseFixMessageConverter extends ProcessingResponseFixMessageConverter[OrderRejectionResponse] {
	override protected def doConvert(response: OrderRejectionResponse): Option[(ExecutionReport, SessionID)] = {
	  def convertFixFields(order: Trade): (ExecType, OrdStatus, Side, OrdType) = {
		(new ExecType(ExecType.REJECTED), new OrdStatus(OrdStatus.REJECTED), FixUtils.side(order.side), FixUtils.ordType(order.orderType))
	  }
	  val rejection = response.rejection
	  val (execType, ordStatus, side, ordType) = convertFixFields(rejection)
	  val executionReport = new ExecutionReport(new OrderID(rejection.clOrdId), new ExecID(rejection.execID.toString), execType, ordStatus, side, new LeavesQty(rejection.leaveQty), new CumQty(rejection.qty - rejection.leaveQty), new AvgPx(0.00))
	  executionReport.set(new LastPx(0.00))
	  executionReport.set(new LastQty(0.00))
	  executionReport.set(new TransactTime(rejection.timestamp.toDate))
	  executionReport.set(ordType)
	  executionReport.set(new OrdRejReason(OrdRejReason.OTHER))
	  executionReport.set(new Account(rejection.clientId))
	  executionReport.set(new Text(rejection.rejectReason))
	  executionReport.set(new Instrument(new Symbol(rejection.symbol)))


	  Some(executionReport, sessionIDByTargetCompID(rejection.connector))
	}
  }

  class OrderValidationRejectionResponseFixMessageConverter extends ProcessingResponseFixMessageConverter[OrderValidationRejectionResponse] {
	override protected def doConvert(response: OrderValidationRejectionResponse): Option[(Message, SessionID)] = {
	  val msgReject = new BusinessMessageReject(new RefMsgType(NewOrderSingle.MSGTYPE), new BusinessRejectReason(BusinessRejectReason.OTHER))
	  val msgText = response.validationRejection.message + " clOrdId: " + response.validationRejection.order.clOrdId + ", connector: " + response.validationRejection.order.connector
	  msgReject.set(new Text(msgText))
	  Some(msgReject, sessionIDByTargetCompID(response.validationRejection.order.connector))
	}
  }

}
