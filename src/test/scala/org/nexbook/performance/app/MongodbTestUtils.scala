package org.nexbook.performance.app

import com.typesafe.config.Config
import org.nexbook.app.AppConfig

/**
  * Created by milczu on 1/2/16.
  */
object MongodbTestUtils {

  import com.mongodb.casbah.Imports._

  lazy val mongodbConfig: Config = AppConfig.mongodbConfig
  lazy val (host, port, db) = (mongodbConfig.getString("host"), mongodbConfig.getInt("port"), mongodbConfig.getString("database"))
  lazy val client = MongoClient(host, port)(db)

  def dropCollection(collection: String) = client(collection).dropCollection()

  def count(collection: String): Int = client(collection).count()

}
