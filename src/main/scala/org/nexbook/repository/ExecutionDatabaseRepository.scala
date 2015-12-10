package org.nexbook.repository

import java.util.Date

import com.mongodb.casbah.Imports
import org.joda.time.DateTime
import org.nexbook.domain._

/**
 * Created by milczu on 09.12.15
 */
class ExecutionDatabaseRepository extends DatabaseRepository[Execution] {

  import com.mongodb.casbah.Imports._

  override protected val collectionName: String = "executions"
  override protected val serialize: (Execution) => MongoDBObject = e => executionConverter(e).serialize(e)
  override protected val deserialize: (MongoDBObject) => Execution = m => executionConverter(m).deserialize(m)

  private def executionConverter(e: Execution): ExecutionConverter = e match {
    case rejection: OrderRejection => OrderRejectionConverter
    case execution: OrderExecution => OrderExecutionConverter
  }

  private def executionConverter(m: MongoDBObject): ExecutionConverter = m.as[String]("class") match {
    case "OrderExecution" => OrderExecutionConverter
    case "OrderRejection" => OrderRejectionConverter
  }

  trait ExecutionConverter {
    protected def serializeBasicFields(e: Execution): MongoDBObject = {
      MongoDBObject("_id" -> e.tradeID, "class" -> e.getClass.getSimpleName, "symbol" -> e.symbol, "clientId" -> e.clientId, "qty" -> e.qty, "side" -> e.side.toString, "orderType" -> e.orderType.toString, "connector" -> e.connector, "timestamp" -> e.timestamp.toDate, "leaveQty" -> e.leaveQty, "clOrdId" -> e.clOrdId, "status" -> e.status.toString, "dealID" -> e.dealID, "execID" -> e.execID, "executionQty" -> e.executionQty, "executionPrice" -> e.executionPrice)
    }

    def serialize(e: Execution): MongoDBObject

    def deserialize(m: MongoDBObject): Execution
  }

  object OrderExecutionConverter extends ExecutionConverter {
    override def serialize(e: Execution): Imports.MongoDBObject = serializeBasicFields(e)

    override def deserialize(m: Imports.MongoDBObject): Execution = new OrderExecution(m.as[Long]("_id"), m.as[Long]("execID"), m.as[Long]("dealID"), m.as[Double]("qty"), m.as[Double]("executionQty"), m.as[Double]("executionPrice"), m.as[Double]("leaveQty"), Side.fromString(m.as[String]("side")), m.as[String]("symbol"), new DateTime(m.as[Date]("timestamp")), m.as[String]("clOrdId"), OrderType.fromString(m.as[String]("orderType")), m.as[String]("clientId"), m.as[String]("connector"))
  }

  object OrderRejectionConverter extends ExecutionConverter {
    override def serialize(e: Execution): Imports.MongoDBObject = serializeBasicFields(e) ++ ("rejectReason" -> e.asInstanceOf[OrderRejection].rejectReason)

    override def deserialize(m: Imports.MongoDBObject): Execution = new OrderRejection(m.as[Long]("_id"), m.as[Long]("execID"), m.as[Long]("dealID"), m.as[Double]("qty"), m.as[Double]("leaveQty"), Side.fromString(m.as[String]("side")), m.as[String]("symbol"), new DateTime(m.as[Date]("timestamp")), m.as[String]("clOrdId"), OrderType.fromString(m.as[String]("orderType")), m.as[String]("clientId"), m.as[String]("connector"), m.as[String]("RejectReason"))
  }

  def findLastExecID: Long = findMaxNumericField("execID")
}
