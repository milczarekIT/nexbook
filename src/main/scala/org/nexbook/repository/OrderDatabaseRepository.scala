package org.nexbook.repository

import org.nexbook.domain.Order

/**
 * Created by milczu on 07.12.15.
 */
class OrderDatabaseRepository extends DatabaseRepository[Order] {

  import com.mongodb.casbah.Imports._
  protected override val collectionName: String = "orders"
  protected override val serialize: (Order) => MongoDBObject = convertToMongoDbObject

  private def convertToMongoDbObject(o: Order): MongoDBObject = {
    MongoDBObject("_id" -> o.sequence, "tradeID" -> o.tradeID, "symbol" -> o.symbol, "clientId" -> o.clientId, "size" -> o.size, "side" -> o.side.toString, "orderType" -> o.orderType.toString, "fixId" -> o.fixId, "timestamp" -> o.timestamp.toDate)
  }
}
