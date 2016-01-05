package org.nexbook.utils

import org.joda.time.{DateTime, DateTimeZone}
import org.nexbook.domain._
import org.scalatest._

import scala.collection.immutable.List

/**
  * Created by milczu on 25.08.15.
  */
class NewOrderValidatorTest extends FlatSpec with Matchers {

  def sampleOrder(symbol: String = "EUR/PLN", size: Double = 1000.00): NewOrder = NewMarketOrder("a", symbol, "cl1", Buy, size, "FIX_ID", DateTime.now(DateTimeZone.UTC))

  def createValidator(allowedSymbolsList: List[String]): NewOrderValidator = {
	new NewOrderValidator {
	  override def allowedSymbols: List[String] = allowedSymbolsList
	}
  }

  "Order Validator" should "return validation error - now allowed symbol" in {
	val validator = createValidator(List("EUR/PLN"))
	val result = validator validate sampleOrder("EUR/USD")
	result shouldBe 'failure
	result.failed.get.getMessage should startWith("Not supported instrument: ")
  }

  "Order Validator" should "return validation error - incorrect size" in {
	val validator = createValidator(List("EUR/PLN"))
	val result = validator validate sampleOrder(size = -100.00)

	result.failed.get should have message "Invalid order size"
  }

  "Order Validator" should "return None - valid order" in {
	val validator = createValidator(List("EUR/PLN"))
	val result = validator validate sampleOrder()
	result shouldBe 'success
  }


}
