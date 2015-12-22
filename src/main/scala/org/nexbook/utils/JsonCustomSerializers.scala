package org.nexbook.utils

import net.liftweb.json._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.nexbook.domain.{OrderStatus, OrderType, Side}

/**
  * Created by milczu on 07.12.15
  */
object JsonCustomSerializers {
  val all = List(OrderTypeSerializer, SideSerializer, OrderStatusSerializer, DateTimeSerializer)
}

case object OrderTypeSerializer extends CustomSerializer[OrderType](format => ( {
  case JString(o) => OrderType.fromString(o)
  case JNull => null
}, {
  case o: OrderType => JString(o.toString)
}))

case object SideSerializer extends CustomSerializer[Side](format => ( {
  case JString(s) => Side.fromString(s)
  case JNull => null
}, {
  case s: Side => JString(s.toString)
}))

case object OrderStatusSerializer extends CustomSerializer[OrderStatus](format => ( {
  case JString(o) => OrderStatus.fromString(o)
  case JNull => null
}, {
  case o: OrderStatus => JString(o.toString)
}))

case object DateTimeSerializer extends CustomSerializer[DateTime](format => ( {
  case JString(s) => new DateTime(ISODateTimeFormat.dateTimeParser().parseDateTime(s))
  case JNull => null
}, {
  case d: DateTime => JString(d.toString(ISODateTimeFormat.dateTime()))
}))

class CustomSerializer[A: Manifest](ser: Formats => (PartialFunction[JValue, A], PartialFunction[Any, JValue])) extends Serializer[A] {
  val Class = implicitly[Manifest[A]].erasure

  def deserialize(implicit format: Formats) = {
	case (TypeInfo(Class, _), json) =>
	  if (ser(format)._1.isDefinedAt(json)) ser(format)._1(json)
	  else throw new MappingException("Can't convert " + json + " to " + Class)
  }

  def serialize(implicit format: Formats) = ser(format)._2
}
