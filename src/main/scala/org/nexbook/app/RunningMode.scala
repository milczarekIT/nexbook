package org.nexbook.app

/**
  * Created by milczu on 1/2/16.
  */
sealed trait RunningMode

case object Test extends RunningMode

case object Live extends RunningMode

object RunningMode {

  def fromString(s: String): RunningMode = s match {
	case "test" => Test
	case "live" => Live
	case _ => throw new IllegalArgumentException(s"Unexpected running mode: $s")
  }
}
