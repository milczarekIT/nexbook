package org.nexbook.handler

import com.typesafe.config.ConfigFactory
import org.nexbook.domain._
import org.nexbook.orderprocessing.response.{OrderExecutionResponse, OrderProcessingResponse, OrderRejectionResponse, OrderValidationRejectionResponse}
import org.nexbook.utils.FixUtils
import org.slf4j.LoggerFactory
import quickfix.field.{Side, _}
import quickfix.fix44.component.Instrument
import quickfix.fix44.{ExecutionReport, Message}
import quickfix.{Session, SessionID, SessionSettings}

import scala.collection.JavaConverters._

/**
 * Created by milczu on 06.12.15.
 */
class ResponseFixResponseSender extends ResponseHandler {

  val logger = LoggerFactory.getLogger(classOf[ResponseFixResponseSender])
  val fixSessionSettings = new SessionSettings(ConfigFactory.load().getString("org.nexbook.fix.config.path"))

  override def handle(response: OrderProcessingResponse) = {
    toFixMessages(response).foreach { x => val (message, sessionID) = x; Session.sendToTarget(message, sessionID)}
  }

  def toFixMessages(response: OrderProcessingResponse): List[(Message, SessionID)] = response match {
    case execution: OrderExecutionResponse => convert(execution)
    case rejection: OrderRejectionResponse => convert(rejection)
    case validationRejection: OrderValidationRejectionResponse => {
      logger.info("TODO")
      List()
    }
  }

  def sessionIDByTargetCompID(fixId: String): SessionID = fixSessionSettings.sectionIterator().asScala.find(_.getTargetCompID.equals(fixId)).get

  def convert(execution: OrderExecutionResponse): List[(ExecutionReport, SessionID)] = {
    def toExecutionReport(order: Order, dealDone: DealDone): (ExecutionReport, SessionID) = {
      def convertFixFields(order: Order): (ExecType, OrdStatus, Side, OrdType) = {
        val side = FixUtils.side(order)
        val ordType = FixUtils.ordType(order)
        val (execType, ordStatus) = if (order.leaveQty == 0.00) (new ExecType(ExecType.TRADE), new OrdStatus(OrdStatus.FILLED)) else (new ExecType(ExecType.TRADE), new OrdStatus(OrdStatus.PARTIALLY_FILLED))
        (execType, ordStatus, side, ordType)
      }
      val (execType, ordStatus, side, ordType) = convertFixFields(order)
      val executionReport = new ExecutionReport(new OrderID(order.clOrdId), new ExecID(execution.dealDone.execID.toString), execType, ordStatus, side, new LeavesQty(order.leaveQty), new CumQty(order.qty), new AvgPx(dealDone.dealPrice))
      val sessionID = sessionIDByTargetCompID(order.connector)

      executionReport.set(new LastPx(dealDone.dealPrice))
      executionReport.set(new LastQty(dealDone.dealSize))
      executionReport.set(new TransactTime(dealDone.executionTime.toDate))
      executionReport.set(new Account(order.clientId))
      executionReport.set(ordType)
      executionReport.set(new Instrument(new Symbol(order.symbol)))

      (executionReport, sessionID)
    }
    val dealDone = execution.dealDone
    List(toExecutionReport(dealDone.buy, dealDone), toExecutionReport(dealDone.sell, dealDone))
  }

  def convert(rejection: OrderRejectionResponse): List[(ExecutionReport, SessionID)] = {
    def convertFixFields(order: Order): (ExecType, OrdStatus, Side, OrdType) = {
      (new ExecType(ExecType.REJECTED), new OrdStatus(OrdStatus.REJECTED), FixUtils.side(order), FixUtils.ordType(order))
    }
    val rejectionDetails = rejection.rejection
    val order = rejectionDetails.order
    val (execType, ordStatus, side, ordType) = convertFixFields(order)
    val executionReport = new ExecutionReport(new OrderID(order.clOrdId), new ExecID(rejectionDetails.execID.toString), execType, ordStatus, side, new LeavesQty(order.leaveQty), new CumQty(order.qty), new AvgPx(0.00))
    executionReport.set(new LastPx(0.00))
    executionReport.set(new LastQty(0.00))
    executionReport.set(new TransactTime(rejectionDetails.rejectDateTime.toDate))
    executionReport.set(ordType)
    executionReport.set(new OrdRejReason(OrdRejReason.OTHER))
    executionReport.set(new Account(order.clientId))
    executionReport.set(new Text(rejectionDetails.rejectReason))
    executionReport.set(new Instrument(new Symbol(order.symbol)))


    List((executionReport, sessionIDByTargetCompID(order.connector)))
  }
}
