package org.nexbook.fix

import quickfix._
import quickfix.fix50.NewOrderSingle

class FixOrderHandler extends MessageCracker with Application {

  override def onCreate(sessionId: SessionID) {
    println("FixOrderHandler Session Created with SessionID = " + sessionId)
  }


  override def onLogon(sessionId: SessionID) {
    println("Logon: " + sessionId)
  }

  override def onLogout(sessionId: SessionID) {
    println("Logout: " + sessionId)
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
    println("toAdmin: " + message)
  }

  @throws(classOf[RejectLogon])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromAdmin(message: Message, sessionId: SessionID) {
    println("fromAdmin: " + message)
  }

  @throws(classOf[DoNotSend])
  override def toApp(message: Message, sessionId: SessionID) {
    println("toApp: " + message)
  }

  @throws(classOf[UnsupportedMessageType])
  @throws(classOf[IncorrectTagValue])
  @throws(classOf[IncorrectDataFormat])
  @throws(classOf[FieldNotFound])
  override def fromApp(message: Message, sessionId: SessionID) {
    println("fromApp - crack: " + message)
    crack(message, sessionId)
  }

  def onMessage(order: NewOrderSingle, sessionId: SessionID) {
    println("Handled order: " + order)
  }

}
