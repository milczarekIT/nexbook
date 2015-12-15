package org.nexbook.orderprocessing.handler

import net.liftweb.json.Extraction._
import net.liftweb.json.Serialization.write
import net.liftweb.json.ext.JodaTimeSerializers
import org.nexbook.orderprocessing.ProcessingResponseHandler
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.utils.JsonCustomSerializers
import org.slf4j.LoggerFactory


/**
 * Created by milczu on 06.12.15
 */
class JsonFileLogger extends ProcessingResponseHandler {

  val logger = LoggerFactory.getLogger("TRADES_LOG")

  implicit val formats = net.liftweb.json.DefaultFormats ++ JsonCustomSerializers.all

  def buildLogLine(payload: AnyRef): String = {
    def asJson(a: AnyRef): String = write(decompose(a))
    payload.getClass.getSimpleName + ":" + asJson(payload)
  }

  override def handle(response: OrderProcessingResponse) = logger.debug(buildLogLine(response.payload))

}