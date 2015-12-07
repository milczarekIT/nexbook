package org.nexbook.utils

import net.liftweb.json._
import org.nexbook.domain.{OrderType, Side}

/**
 * Created by milczu on 07.12.15.
 */
object JsonCustom {
  val allCustomFormats = List(OrderTypeSerializer, SideSerializer)

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

  class CustomSerializer[A: Manifest](ser: Formats => (PartialFunction[JValue, A], PartialFunction[Any, JValue])) extends Serializer[A] {
    val Class = implicitly[Manifest[A]].erasure

    def deserialize(implicit format: Formats) = {
      case (TypeInfo(Class, _), json) =>
        if (ser(format)._1.isDefinedAt(json)) ser(format)._1(json)
        else throw new MappingException("Can't convert " + json + " to " + Class)
    }

    def serialize(implicit format: Formats) = ser(format)._2
  }
}
