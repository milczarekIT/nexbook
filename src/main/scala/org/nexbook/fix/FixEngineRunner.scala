package org.nexbook.fix

import quickfix._

/**
  * Created by milczu on 25.08.15.
  */
class FixEngineRunner(fixOrderHandler: FixMessageHandler, configPath: String) extends FixApplicationRunner with FixFileBasedConfigurer {

  val fixOrderHandlerSessionSettings = new SessionSettings(configPath)

  override protected def application: Application = fixOrderHandler

  override protected def sessionSettings: SessionSettings = fixOrderHandlerSessionSettings
}
