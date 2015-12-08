package org.nexbook.fix

import org.nexbook.core.OrderHandler
import org.nexbook.domain.Order
import org.slf4j.LoggerFactory
import quickfix._
import quickfix.fix44.NewOrderSingle

class FixOrderHandler(orderHandler: OrderHandler) extends MessageCracker with Application {

  val LOGGER = LoggerFactory.getLogger(classOf[FixOrderHandler])

  override def onCreate(sessionId: SessionID) {
    LOGGER.info("FixOrderHandler Session Created with SessionID = {}", sessionId)
  }

  override def onLogon(sessionId: SessionID) {
    LOGGER.info("Logon: {}", sessionId)
  }

  override def onLogout(sessionId: SessionID) {
    LOGGER.info("Logout: {}", sessionId)
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
    LOGGER.trace("ToAdmin: {}", message)
  }

  @throws(classOf[RejectLogon])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromAdmin(message: Message, sessionId: SessionID) {
    LOGGER.debug("FromAdmin: {}", message)
  }

  @throws(classOf[DoNotSend])
  override def toApp(message: Message, sessionId: SessionID) {
    LOGGER.trace("ToApp: {}", message)
  }

  @throws(classOf[UnsupportedMessageType])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromApp(message: Message, sessionId: SessionID) {
    LOGGER.trace("FromApp: {}", message)
    crack(message, sessionId)
  }

  def onMessage(order: NewOrderSingle, sessionId: SessionID) {
    LOGGER.trace("HandledOrder ClOrdID: " + order.getClOrdID.getValue + ", symbol: " + order.getSymbol.getValue + ", orderQty: " + order.getOrderQty.getValue + ", order: " + order)
    orderHandler.handle(FixOrderConverter convert order)

  }

}
