package org.nexbook.app

import com.typesafe.config.{Config, ConfigFactory}
import org.nexbook.utils.{Clock, DefaultClock}
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 23.12.15.
  */
class AppConfig {

  val logger = LoggerFactory.getLogger(classOf[AppConfig])
  val defaultConfigName = "nexbook"
  val rootConfig: Config = resolveAppConfig

  def resolveAppConfig: Config = {
	val configName = Option[String](System.getProperty("config.name")) match {
	  case None =>
		logger.warn(s"VM property 'config.name' not defined. Running with default config: $defaultConfigName")
		defaultConfigName
	  case Some(cn) => cn
	}
	ConfigFactory.load(s"config/$configName").getConfig("nexbook")
  }
}

object AppConfig {

  private val rootConfig = new AppConfig().rootConfig

  lazy val supportedCurrencyPairs = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")

  lazy val clock: Clock = new DefaultClock

  lazy val fixConfig = rootConfig.getConfig("fix")

  lazy val fixConfigPath = fixConfig.getString("configPath")

  lazy val mode: Mode = Mode.fromString(rootConfig.getString("mode"))

  lazy val mongodbConfig = rootConfig.getConfig("mongodb")
}
