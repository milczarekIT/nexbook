package org.nexbook.config

/**
  * Created by milczu on 25.08.15.
  */
object ConfigFactory {
  val supportedCurrencyPairs = initSupportedCurrencyPairs

  private def initSupportedCurrencyPairs = List("EUR/USD", "AUD/USD", "GBP/USD", "USD/JPY", "EUR/JPY", "EUR/GBP", "USD/CAD", "USD/CHF")
}
