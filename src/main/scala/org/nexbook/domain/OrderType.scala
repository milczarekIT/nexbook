package org.nexbook.domain

sealed trait OrderType

case object Limit extends OrderType

case object Market extends OrderType
