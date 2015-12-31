package org.nexbook.repository

import com.typesafe.config.Config
import org.nexbook.app.AppConfig

/**
  * Created by milczu on 07.12.15.
  */
trait DatabaseRepository[T] {

  import com.mongodb.casbah.Imports._

  type Serialize = T => MongoDBObject
  type Deserialize = MongoDBObject => T

  lazy val mongodbConfig: Config = AppConfig.mongodbConfig
  lazy val (host, port, db) = (mongodbConfig.getString("host"), mongodbConfig.getInt("port"), mongodbConfig.getString("database"))
  lazy val client = MongoClient(host, port)(db)
  val findAllLimit = 10000

  protected val collectionName: String

  protected def collection = client(collectionName)

  protected val serialize: Serialize
  protected val deserialize: Deserialize

  def add(obj: T): Unit = {
	collection insert serialize(obj)
  }

  def findAll(): List[T] = {
	val cursor = collection.find().sort(MongoDBObject("_id" -> -1)).limit(findAllLimit)
	(for (x <- cursor) yield deserialize(x)).toList
  }

  protected def findMaxNumericField(fieldName: String): Long = {
	val q = MongoDBObject.empty
	val fields = MongoDBObject(fieldName -> 1)
	val descId = MongoDBObject(fieldName -> -1)
	val cursor = collection.find(q, fields).sort(descId).limit(1)
	if (cursor.isEmpty) 0 else cursor.next().as[Long](fieldName)
  }
}
