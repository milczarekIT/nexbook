package org.nexbook.repository

import java.util.Date

import com.mongodb.casbah.Imports
import org.joda.time.DateTime
import org.nexbook.domain._

/**
  * Created by milczu on 07.12.15.
  */
class OrderDatabaseRepository extends DatabaseRepository[Order] with OrderRepository {

  import com.mongodb.casbah.Imports._

  override protected val collectionName: String = "orders"
  override protected val serialize: Serialize = convertToMongoDbObject
  override protected val deserialize: Deserialize = convertFromMongoDBObject

  private def convertToMongoDbObject(o: Order): MongoDBObject = o match {
	case c: OrderCancel => OrderCancelConverter.serialize(c)
	case _ => OrderConverter.serialize(o)
  }

  trait BaseOrderConverter {
	protected def serializeBasicFields(o: Order): MongoDBObject = {
	  val dbObject = MongoDBObject("_id" -> o.tradeID, "class" -> o.getClass.getSimpleName, "symbol" -> o.symbol, "clientId" -> o.clientId, "qty" -> o.qty, "side" -> o.side.toString, "orderType" -> o.orderType.toString, "connector" -> o.connector, "timestamp" -> o.timestamp.toDate, "leaveQty" -> o.leaveQty, "clOrdId" -> o.clOrdId, "status" -> o.status.toString)
	  if (o.orderType == Limit && o.isInstanceOf[LimitOrder]) {
		dbObject ++ ("limit" -> o.asInstanceOf[LimitOrder].limit)
	  }
	  dbObject
	}

	protected def deserializeBasicFields(m: MongoDBObject, tradeIDPropertyName: String, clOrdIdPropertyName: String): Order = {
	  OrderType.fromString(m.as[String]("orderType")) match {
		case Limit => new LimitOrder(m.as[Long](tradeIDPropertyName), m.as[String]("symbol"), m.as[String]("clientId"), Side.fromString(m.as[String]("side")), m.as[Double]("qty"), m.getAsOrElse[Double]("limit", 0.00), m.as[String]("connector"), new DateTime(m.as[Date]("timestamp")), m.as[String](clOrdIdPropertyName))
		case Market => new MarketOrder(m.as[Long](tradeIDPropertyName), m.as[String]("symbol"), m.as[String]("clientId"), Side.fromString(m.as[String]("side")), m.as[Double]("qty"), m.as[String]("connector"), new DateTime(m.as[Date]("timestamp")), m.as[String](clOrdIdPropertyName))
	  }
	}

	def serialize(order: Order): MongoDBObject

	def deserialize(m: MongoDBObject): Order
  }

  object OrderConverter extends BaseOrderConverter {
	override def serialize(o: Order): Imports.MongoDBObject = serializeBasicFields(o)

	def deserialize(m: MongoDBObject): Order = deserializeBasicFields(m, "_id", "clOrdId")
  }

  object OrderCancelConverter extends BaseOrderConverter {
	override def serialize(order: Order): Imports.MongoDBObject = {
	  val orderCancel = order.asInstanceOf[OrderCancel]
	  serializeBasicFields(order) ++ ("dealID" -> orderCancel.dealID) ++ ("origClOrdId" -> orderCancel.origClOrdId)
	}

	override def deserialize(m: Imports.MongoDBObject): Order = {
	  val baseOrder = deserializeBasicFields(m, "dealID", "origClOrdId")
	  new OrderCancel(m.as[Long]("_id"), new DateTime(m.as[Date]("timestamp")), m.as[String]("clOrdId"), baseOrder)
	}
  }

  private def convertFromMongoDBObject(m: MongoDBObject): Order = {
	m.as[String]("class") match {
	  case "LimitOrder" | "MarketOrder" => OrderConverter.deserialize(m)
	  case "OrderCancel" => OrderCancelConverter.deserialize(m)
	}
  }

  def findLastTradeID: Long = findMaxNumericField("_id")

  override def findBy(clOrdId: String, connector: String): Option[Order] = {
	collection.findOne(MongoDBObject("clOrdId" -> clOrdId, "connector" -> connector)).map(o => deserialize(o))
  }

  override def updateStatus(tradeID: Long, status: OrderStatus, prevStatus: OrderStatus): Boolean = {
	val query = MongoDBObject("_id" -> tradeID, "status" -> prevStatus.toString)
	val update = MongoDBObject("status" -> status.toString)
	collection.findAndModify(query, update).nonEmpty
  }

  def updateStatusAndLeaveQty(tradeID: Long, status: OrderStatus, prevStatus: OrderStatus, prevLeaveQty: Double, leaveQty: Double): Boolean = {
	val query = MongoDBObject("_id" -> tradeID, "status" -> prevStatus.toString, "leaveQty" -> prevLeaveQty)
	val update = MongoDBObject("status" -> status.toString, "leaveQty" -> leaveQty)
	collection.findAndModify(query, update).nonEmpty
  }
}
