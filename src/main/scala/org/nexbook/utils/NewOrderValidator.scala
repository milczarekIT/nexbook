package org.nexbook.utils

import org.nexbook.app.AppConfig
import org.nexbook.domain.NewOrder

import scala.util.{Failure, Success, Try}

/**
  * Created by milczu on 25.08.15.
  */
class ValidationException(message: String) extends Exception(message)

class NewOrderValidator {

  type OrderValidation = NewOrder => Try[NewOrder]

  val symbolValidation: OrderValidation = order => if (allowedSymbols.contains(order.symbol)) Success(order) else Failure(new ValidationException("Not supported instrument: " + order.symbol))
  val sizeValidation: OrderValidation = order => if (order.qty > 0.00) Success(order) else Failure(new ValidationException("Invalid order size"))

  val defaultValidations = List(symbolValidation, sizeValidation)

  def allowedSymbols = AppConfig.supportedCurrencyPairs

  def validate(order: NewOrder): Try[NewOrder] = {
	def validate(order: NewOrder, validations: List[OrderValidation]): Try[NewOrder] = validations match {
	  case List() => Success(order)
	  case _ =>
		validations.head(order) match {
		  case Success(o) => validate(o, validations.tail)
		  case Failure(e) => Failure(e)
		}
	}
	validate(order, defaultValidations)
  }


}
