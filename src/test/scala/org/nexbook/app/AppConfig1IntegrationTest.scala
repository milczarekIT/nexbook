package org.nexbook.app

import org.nexbook.tags.Integration
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by milczu on 01.01.16.
  */
class AppConfig1IntegrationTest extends FlatSpec with Matchers {

  "App Config" should "return name empty1" taggedAs Integration in {
	System.setProperty("config.name", "empty1")

	AppConfig.configName should equal("empty1")
  }

}
