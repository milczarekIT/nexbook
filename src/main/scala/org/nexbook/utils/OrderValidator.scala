package org.nexbook.utils

import org.nexbook.config.ConfigFactory
import org.nexbook.domain.NewOrder

/**
 * Created by milczu on 25.08.15.
 */
case class ValidationError(message: String)

class OrderValidator {

  type OrderValidation = NewOrder => Option[ValidationError]

  val symbolValidation: OrderValidation = order => if (allowedSymbols.contains(order.symbol)) None else Some(ValidationError("Not supported instrument: " + order.symbol))
  val sizeValidation: OrderValidation = order => if (order.qty > 0.00) None else Some(ValidationError("Invalid order size"))

  val defaultValidations = List(symbolValidation, sizeValidation)

  def allowedSymbols = ConfigFactory.supportedCurrencyPairs

  def validate(order: NewOrder): Option[ValidationError] = {
    def validate(order: NewOrder, validations: List[OrderValidation]): Option[ValidationError] = validations match {
      case List() => None
      case _ =>
        val result: Option[ValidationError] = validations.head(order)
        result match {
          case None => validate(order, validations.tail)
          case _ => result
        }

    }
    validate(order, defaultValidations)
  }


}
