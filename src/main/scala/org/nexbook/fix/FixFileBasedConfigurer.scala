package org.nexbook.fix

import quickfix._

/**
 * Created by milczu on 25.08.15.
 */
trait FixFileBasedConfigurer {

  protected def sessionSettings: SessionSettings

  protected def logFactory: LogFactory = new FileLogFactory(sessionSettings)

  protected def messageStoreFactory: MessageStoreFactory = new MemoryStoreFactory //new FileStoreFactory(sessionSettings)
}
