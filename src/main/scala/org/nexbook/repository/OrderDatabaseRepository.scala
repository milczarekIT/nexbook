package org.nexbook.repository

import java.util.Date

import org.joda.time.DateTime
import org.nexbook.domain._

/**
 * Created by milczu on 07.12.15.
 */
class OrderDatabaseRepository extends DatabaseRepository[Order] with OrderRepository {

  import com.mongodb.casbah.Imports._

  override protected val collectionName: String = "orders"
  override protected val serialize: (Order) => MongoDBObject = convertToMongoDbObject
  override protected val deserialize: (MongoDBObject) => Order = convertFromMongoDBObject

  private def convertToMongoDbObject(o: Order): MongoDBObject = {
    MongoDBObject("_id" -> o.tradeID, "symbol" -> o.symbol, "clientId" -> o.clientId, "qty" -> o.qty, "side" -> o.side.toString, "orderType" -> o.orderType.toString, "connector" -> o.connector, "timestamp" -> o.timestamp.toDate, "leaveQty" -> o.leaveQty, "clOrdId" -> o.clOrdId)
  }

  private def convertFromMongoDBObject(m: MongoDBObject): Order = {
    val orderType = OrderType.fromString(m.getAs[String]("ordType").get)
    orderType match {
      case Limit => new LimitOrder(m.as[Long]("_id"), m.as[String]("symbol"), m.as[String]("clientId"), Side.fromString(m.as[String]("side")), m.as[Double]("qty"), m.as[Double]("limit"), m.as[String]("connector"), new DateTime(m.as[Date]("timestamp")), m.as[String]("clOrdId"))
      case Market => new MarketOrder(m.as[Long]("_id"), m.as[String]("symbol"), m.as[String]("clientId"), Side.fromString(m.as[String]("side")), m.as[Double]("qty"), m.as[String]("connector"), new DateTime(m.as[Date]("timestamp")), m.as[String]("clOrdId"))
    }
  }

  def findLastTradeID: Long = {
    val q = MongoDBObject.empty
    val fields = MongoDBObject("_id" -> 1)
    val descId = MongoDBObject("_id" -> -1)
    val cursor = collection.find(q, fields).sort(descId).limit(1)
    if (cursor.isEmpty) 0 else cursor.next().getAsOrElse[Long]("_id", 0)
  }

}
