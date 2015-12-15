package org.nexbook.utils

import org.joda.time.DateTime

/**
 * Created by milczu on 07.12.15.
 */
trait Clock {

  def currentDateTime: DateTime
}
