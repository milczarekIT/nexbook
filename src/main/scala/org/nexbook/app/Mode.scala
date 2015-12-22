package org.nexbook.app

/**
  * Created by milczu on 12/21/15.
  */
sealed trait Mode

case object Akka extends Mode

case object PubSub extends Mode

object Mode {
  def fromString(s: String): Mode = s match {
	case "Akka" => Akka
	case "PubSub" => PubSub
	case _ => throw new IllegalArgumentException(s"Invalid arg: $s")
  }
}
