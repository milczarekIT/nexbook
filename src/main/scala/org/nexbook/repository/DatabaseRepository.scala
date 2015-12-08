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

  protected val collectionName: String

  protected def collection = client(collectionName)

  protected val serialize: (T => MongoDBObject)

  def add(obj: T): Unit = {
    collection insert serialize(obj)
  }
}
