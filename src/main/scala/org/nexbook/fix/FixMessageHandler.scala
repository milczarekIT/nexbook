package org.nexbook.fix

import org.nexbook.app.OrderHandlersModule
import org.nexbook.neworderhandler.OrderCancelHandler
import org.slf4j.LoggerFactory
import quickfix._
import quickfix.fix44.{NewOrderSingle, OrderCancelRequest}

class FixMessageHandler(orderHandlersModule: OrderHandlersModule, orderCancelHandler: OrderCancelHandler, fixOrderConverter: FixOrderConverter) extends Application {

  val logger = LoggerFactory.getLogger(classOf[FixMessageHandler])
  val newOrderHandlers = orderHandlersModule.newOrderHandlers
  val newOrderCancelHandlers = orderHandlersModule.newOrderCancelsHandlers

  override def onCreate(sessionId: SessionID) {
    logger.info("FixOrderHandler Session Created with SessionID = {}", sessionId)
  }

  override def onLogon(sessionId: SessionID) {
    logger.info("Logon: {}", sessionId)
  }

  override def onLogout(sessionId: SessionID) {
    logger.info("Logout: {}", sessionId)
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
    logger.trace("ToAdmin: {}", message)
  }

  @throws(classOf[RejectLogon])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromAdmin(message: Message, sessionId: SessionID) {
    logger.debug("FromAdmin: {}", message)
  }

  @throws(classOf[DoNotSend])
  override def toApp(message: Message, sessionId: SessionID) {
    logger.trace("ToApp: {}", message)
  }

  @throws(classOf[UnsupportedMessageType])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromApp(message: Message, sessionId: SessionID) {
    logger.trace("FromApp: {}", message)
    try {
      message match {
        case o: NewOrderSingle => onMessage(o, sessionId)
        case o: OrderCancelRequest => onMessage(o, sessionId)
      }
    } catch {
      case e: Exception => logger.error("Unexpected exception", e)
    }
  }

  def onMessage(order: NewOrderSingle, sessionId: SessionID) {
    logger.debug("Handled Order ClOrdID: " + order.getClOrdID.getValue + ", symbol: " + order.getSymbol.getValue + ", orderQty: " + order.getOrderQty.getValue + ", order: " + order)
    newOrderHandlers.foreach(_.handle(fixOrderConverter convert order))
  }

  def onMessage(orderCancel: OrderCancelRequest, sessionId: SessionID) = {
    logger.debug("Handled OrderCancel origClOrdID: {}, new clOrdID: {}, from: {}", orderCancel.getOrigClOrdID.getValue, orderCancel.getClOrdID.getValue, sessionId.getTargetCompID)
    newOrderCancelHandlers.foreach(_.handle(fixOrderConverter convert orderCancel))
  }

}
