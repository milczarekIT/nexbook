package org.nexbook.utils

import org.nexbook.domain.Order
import org.scalatest._

import scala.collection.immutable.List

/**
 * Created by milczu on 25.08.15.
 */
class OrderValidatorTest extends FlatSpec with Matchers{

  def sampleOrder: Order = ???

  def createValidator(allowedSymbolsList: List[String]): OrderValidator = {
    new OrderValidator {
      override def allowedSymbols: List[String] = allowedSymbolsList
    }
  }

  "Order Validator" should "return validation error" in {
    import org.scalatest.OptionValues._
    val validator = createValidator(List("EUR/PLN"))
    val result = validator validate sampleOrder
    result should not be None
    result.value.message should not be (null)
  }

}
