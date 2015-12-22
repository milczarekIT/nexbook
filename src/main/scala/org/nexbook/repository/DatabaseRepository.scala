package org.nexbook.repository

import com.typesafe.config.ConfigFactory

/**
  * Created by milczu on 07.12.15.
  */
trait DatabaseRepository[T] {

  import com.mongodb.casbah.Imports._

  val dbConfig = ConfigFactory.load().getConfig("org.nexbook.mongo")
  val (host, port, db) = (dbConfig.getString("host"), dbConfig.getInt("port"), dbConfig.getString("dbName"))
  val client = MongoClient(host, port)(db)
  val findAllLimit = 10000

  protected val collectionName: String

  protected def collection = client(collectionName)

  protected val serialize: (T => MongoDBObject)
  protected val deserialize: (MongoDBObject => T)

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
