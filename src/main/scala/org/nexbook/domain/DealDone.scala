package org.nexbook.domain

import org.joda.time.DateTime

/**
 * Created by milczu on 18.08.15.
 */
case class DealDone(execID: Long, buy: Order, sell: Order, dealSize: Double, dealPrice: Double, executionTime: DateTime) extends ProcessingResponse
