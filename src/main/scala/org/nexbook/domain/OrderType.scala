package org.nexbook.domain

trait OrderType

case object Limit extends  OrderType

case object Market extends OrderType
