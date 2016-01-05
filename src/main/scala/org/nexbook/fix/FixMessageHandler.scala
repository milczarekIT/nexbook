package org.nexbook.fix

import org.nexbook.app.OrderHandlersModule
import org.slf4j.LoggerFactory
import quickfix._
import quickfix.fix44.{NewOrderSingle, OrderCancelRequest}

class FixMessageHandler(orderHandlersModule: OrderHandlersModule, fixOrderConverter: FixOrderConverter) extends Application {

  val logger = LoggerFactory.getLogger(classOf[FixMessageHandler])
  val newOrderHandlers = orderHandlersModule.newOrderHandlers

  override def onCreate(sessionId: SessionID) {
	logger.info(s"FixOrderHandler Session Created with SessionID: $sessionId")
  }

  override def onLogon(sessionId: SessionID) {
	logger.info(s"Logon: $sessionId")
  }

  override def onLogout(sessionId: SessionID) {
	logger.info(s"Logout: $sessionId")
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
	logger.trace(s"ToAdmin: $message")
  }

  @throws(classOf[RejectLogon])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromAdmin(message: Message, sessionId: SessionID) {
	logger.debug(s"FromAdmin: $message")
  }

  @throws(classOf[DoNotSend])
  override def toApp(message: Message, sessionId: SessionID) {
	logger.trace(s"ToApp: $message")
  }

  @throws(classOf[UnsupportedMessageType])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromApp(message: Message, sessionId: SessionID) {
	logger.trace(s"FromApp: ${System.currentTimeMillis}: $message")
	try {
	  message match {
		case o: NewOrderSingle => onMessage(o, sessionId)
		case o: OrderCancelRequest => onMessage(o, sessionId)
	  }
	} catch {
	  case e: Exception => logger.error("Unexpected exception", e)
	}
  }

  def onMessage(order: NewOrderSingle, sessionID: SessionID) {
	logger.debug(s"onMessage: ${System.currentTimeMillis} handled message $order from: ${sessionID.getTargetCompID}")
	newOrderHandlers.foreach(_.handleNewOrder(fixOrderConverter convert order))
  }

  def onMessage(orderCancel: OrderCancelRequest, sessionID: SessionID) = {
	logger.debug(s"onMessage: ${System.currentTimeMillis} handled message $orderCancel from: ${sessionID.getTargetCompID}")
	newOrderHandlers.foreach(_.handleNewOrderCancel(fixOrderConverter convert orderCancel))
  }

}
