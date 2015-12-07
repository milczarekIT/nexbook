package org.nexbook.handler

import org.nexbook.orderprocessing.response.OrderProcessingResponse
import org.slf4j.LoggerFactory

/**
 * Created by milczu on 06.12.15.
 */
class GeneralResponseHandler(handlers: List[ResponseHandler]) {

  val logger = LoggerFactory.getLogger(classOf[GeneralResponseHandler])

  def doHandle(response: OrderProcessingResponse) = {
    logger.info("Response: {}", response)
    handlers.foreach(_.handle(response))
  }

}
