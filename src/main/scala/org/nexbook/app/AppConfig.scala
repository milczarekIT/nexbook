package org.nexbook.app

import com.typesafe.config.{Config, ConfigFactory}
import org.nexbook.utils.{Clock, DefaultClock}
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 23.12.15.
  */
class AppConfig {
  import AppConfig._

  val logger = LoggerFactory.getLogger(classOf[AppConfig])

  protected val (configName, rootConfig): (String, Config) = resolveAppConfig

  def resolveAppConfig: (String, Config) = {
	val configName = Option[String](System.getProperty("config.name")) match {
	  case None =>
		logger.warn(s"VM property 'config.name' not defined. Running with default config: $defaultConfigName")
		defaultConfigName
	  case Some(cn) => cn
	}
	(configName, ConfigFactory.load(s"config/$configName").getConfig("nexbook"))
  }

  def init(): (String, Config) = (configName, rootConfig)
}

object AppConfig {
  protected val defaultConfigName = "nexbook"

  private val (name, rootConfig) = new AppConfig().init()

  val supportedCurrencyPairs = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")

  lazy val clock: Clock = new DefaultClock

  lazy val fixConfig = rootConfig.getConfig("fix")

  lazy val fixConfigPath = fixConfig.getString("configPath")

  lazy val mode: Mode = Mode.fromString(rootConfig.getString("mode"))

  lazy val mongodbConfig = rootConfig.getConfig("mongodb")

  val configName = name
}
