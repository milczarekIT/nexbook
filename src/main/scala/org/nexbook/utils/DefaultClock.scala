package org.nexbook.utils

import org.joda.time.{DateTime, DateTimeZone}

/**
 * Created by milczu on 07.12.15.
 */
class DefaultClock extends Clock {

  override def getCurrentDateTime: DateTime = DateTime.now(DateTimeZone.UTC)
}
