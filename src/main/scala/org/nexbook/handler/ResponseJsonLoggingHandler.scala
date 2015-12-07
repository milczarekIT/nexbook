package org.nexbook.handler

import net.liftweb.json.Extraction._
import net.liftweb.json.Serialization.write
import net.liftweb.json._
import net.liftweb.json.ext.JodaTimeSerializers
import org.nexbook.domain.{ProcessingResponse, Side}
import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.nexbook.utils.JsonCustom
import org.nexbook.utils.JsonCustom.SideSerializer
import org.slf4j.LoggerFactory


/**
 * Created by milczu on 06.12.15.
 */
class ResponseJsonLoggingHandler extends ResponseHandler {

  val logger = LoggerFactory.getLogger(classOf[ResponseJsonLoggingHandler])

  def buildLogLine(payload: ProcessingResponse): String = {
    implicit val formats = net.liftweb.json.DefaultFormats ++ JsonCustom.allCustomFormats ++ JodaTimeSerializers.all
    def asJson(a: AnyRef): String = write(decompose(a))
    payload.getClass.getSimpleName + ":" + asJson(payload)
  }

  override def handle(response: OrderProcessingResponse) = logger.debug(buildLogLine(response.payload))







}
