package org.nexbook.repository

/**
  * Created by milczu on 03.01.16.
  */
sealed trait RepositoryCollectionType

case object Mutable extends RepositoryCollectionType
case object Immutable extends RepositoryCollectionType

object RepositoryCollectionType {
  def fromString(s: String): RepositoryCollectionType = s match {
	case "mutable" => Mutable
	case "immutable" => Immutable
	case _ => throw new IllegalArgumentException(s"Unexpected type: $s")
  }

}
