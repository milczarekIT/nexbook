package org.nexbook.domain

import org.joda.time.DateTime

/**
 * Created by milczu on 07.12.15.
 */
case class OrderRejection(execID: Long, order: Order, rejectReason: String, rejectDateTime: DateTime) {

}
