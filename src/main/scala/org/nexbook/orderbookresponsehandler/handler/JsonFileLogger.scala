package org.nexbook.orderbookresponsehandler.handler

import net.liftweb.json.Extraction._
import net.liftweb.json.Serialization.write
import org.nexbook.orderbookresponsehandler.response.OrderBookResponse
import org.nexbook.utils.JsonCustomSerializers
import org.slf4j.LoggerFactory


/**
  * Created by milczu on 06.12.15
  */
class JsonFileLogger extends OrderBookResponseHandler {

  val logger = LoggerFactory.getLogger("TRADES_LOG")

  implicit val formats = net.liftweb.json.DefaultFormats ++ JsonCustomSerializers.all

  override def handle(response: OrderBookResponse) = logger.debug(buildLogLine(response.payload))

  def buildLogLine(payload: AnyRef): String = {
	def asJson(a: AnyRef): String = write(decompose(a))
	payload.getClass.getSimpleName + ":" + asJson(payload)
  }

}
