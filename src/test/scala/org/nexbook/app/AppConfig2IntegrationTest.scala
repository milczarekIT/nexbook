package org.nexbook.app

import org.nexbook.tags.Integration
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by milczu on 1/1/16.
  */
class AppConfig2IntegrationTest extends FlatSpec with Matchers {

  "AppConfig" should "return name empty2" taggedAs Integration in {
	System.setProperty("config.name", "empty2")

	AppConfig.configName should equal("empty2")
  }


}
