package org.nexbook.utils

import org.nexbook.domain._
import org.scalatest._

import scala.collection.immutable.List

/**
 * Created by milczu on 25.08.15.
 */
class OrderValidatorTest extends FlatSpec with Matchers {

  import org.scalatest.OptionValues._
  def sampleOrder(symbol: String = "EUR/PLN", size: Double = 1000.00): Order = MarketOrder("a", symbol, "cl1", Buy, size, Market, "FIX_ID")

  def createValidator(allowedSymbolsList: List[String]): OrderValidator = {
    new OrderValidator {
      override def allowedSymbols: List[String] = allowedSymbolsList
    }
  }

  "Order Validator" should "return validation error - now allowed symbol" in {

    val validator = createValidator(List("EUR/PLN"))
    val result = validator validate sampleOrder("EUR/USD")
    result should not be None
    result.value.message should not be (null)
    result.value.message should startWith("Not supported instrument: ")
  }

  "Order Validator" should "return validation error - incorrect size" in {
    val validator = createValidator(List("EUR/PLN"))
    val result = validator validate sampleOrder(size = -100.00)
    result should not be None
    result.value.message should not be (null)
    result.value.message should equal("Invalid order size")
  }

  "Order Validator" should "return None - valid order" in {
    val validator = createValidator(List("EUR/PLN"))
    val result = validator validate sampleOrder()
    result should be(None)
  }

}
