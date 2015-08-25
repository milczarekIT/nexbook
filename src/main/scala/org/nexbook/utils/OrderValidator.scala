package org.nexbook.utils

import org.nexbook.config.ConfigFactory
import org.nexbook.domain.Order

/**
 * Created by milczu on 25.08.15.
 */
case class ValidationError(message: String)

class OrderValidator {

  type OrderValidation = Order => Option[ValidationError]

  val symbolValidation: OrderValidation = order => if(allowedSymbols.contains(order.symbol)) None else Some(ValidationError("Not supported instrument: " + order.symbol))
  val sizeValidation: OrderValidation = order => if(order.size > 0.00) None else Some(ValidationError("Invalid order size"))

  val defaultValidations = List(symbolValidation, sizeValidation)

  def allowedSymbols = ConfigFactory.supportedCurrencyPairs

  def validate(order: Order): Option[ValidationError] = {
    def validate(order: Order, validations: List[OrderValidation]): Option[ValidationError] = validations match {
      case List() => None
      case _ => {
        val result: Option[ValidationError] = validations.head.apply(order)
        result match {
            case None => validate(order, validations.tail)
            case _ => result
        }
      }

    }
    validate(order, defaultValidations)
  }




}
