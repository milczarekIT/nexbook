package org.nexbook.testutils

import org.joda.time.{DateTime, DateTimeZone}
import quickfix.field.{MsgType, TransactTime}
import quickfix.fix44.{NewOrderSingle, OrderCancelRequest}
import quickfix.{DataDictionary, Message, SessionID}

import scala.io.Source

/**
  * Created by milczu on 1/2/16.
  */
object FixMessageProvider {

  val dataDictionary = new DataDictionary("config/FIX44.xml")
  val defaultTestData = "src/test/resources/data/orders8_100k.fix"

  def get: List[(Message, SessionID)] = get(defaultTestData, None)

  def get(limit: Int): List[(Message, SessionID)] = get(defaultTestData, Some(limit))

  def get(limit: Option[Int]): List[(Message, SessionID)] = get(defaultTestData, limit)

  def get(fileName: String): List[(Message, SessionID)] = get(fileName, None)

  def get(fileName: String, limit: Int): List[(Message, SessionID)] = get(fileName, Some(limit))

  def get(fileName: String, limit: Option[Int]): List[(Message, SessionID)] = {
	def toFixMessage(line: String): Message = new Message(line, dataDictionary, false)

	def fixMsgToSpecializedMsg(msg: Message): Message = {
	  msg.getHeader.getField(new MsgType()).getValue match {
		case NewOrderSingle.MSGTYPE =>
		  val newOrderSingle = new NewOrderSingle
		  newOrderSingle.fromString(msg.toString, dataDictionary, false)
		  newOrderSingle
		case OrderCancelRequest.MSGTYPE =>
		  val orderCancelRequest = new OrderCancelRequest
		  orderCancelRequest.fromString(msg.toString, dataDictionary, false)
		  orderCancelRequest
		case _ => msg
	  }
	}

	def withUpdatedFields(msg: Message): Message = msg.getHeader.getField(new MsgType()).getValue match {
	  case NewOrderSingle.MSGTYPE =>
		val newOrderSingle: NewOrderSingle = msg.asInstanceOf[NewOrderSingle]
		newOrderSingle.set(new TransactTime(DateTime.now(DateTimeZone.UTC).toDate))
		newOrderSingle
	  case OrderCancelRequest.MSGTYPE =>
		val orderCancelRequest: OrderCancelRequest = msg.asInstanceOf[OrderCancelRequest]
		orderCancelRequest.set(new TransactTime(DateTime.now(DateTimeZone.UTC).toDate))
		orderCancelRequest
	}

	def createSessionID(message: Message): SessionID = {
	  val header = message.getHeader
	  val beginString = header.getString(8)
	  val senderCompID = header.getString(49)
	  val targetCompID = header.getString(56)
	  val qualifier = ""
	  new SessionID(beginString, senderCompID, targetCompID, qualifier)
	}


	val lines: List[String] = {
	  val allLines = Source.fromFile(fileName).getLines();
	  limit match {
		case Some(value) => allLines.take(value).toList
		case None => allLines.toList
	  }
	}
	val convert: String => (Message, SessionID) = toFixMessage _ andThen fixMsgToSpecializedMsg andThen withUpdatedFields andThen(msg => (msg, createSessionID(msg)))

	lines.map(convert(_))
  }
}
