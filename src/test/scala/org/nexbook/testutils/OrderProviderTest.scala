package org.nexbook.testutils

import org.nexbook.domain.{Limit, Market, Order, OrderType}
import org.nexbook.tags.Integration
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by milczu on 07.01.16.
  */
class OrderProviderTest extends WordSpec with Matchers {

  type DataType = Order
  
  val testDataDir = "src/test/resources/data"
  val marketOrLimitOrder: DataType => Boolean = o => {
	o.orderType match {
	  case Limit | Market => true
	  case _ => false
	}
  }

  val ordTypeExtractor: DataType => OrderType = o => o.orderType
  val symbolExtractor: DataType => String = o => o.symbol

  val loadData: String => List[DataType] = fileName => OrderProvider.get(fileName)

  "Test data orders4_*.txt" should {
	"return 50 000 orders with 4 symbols for file orders4_050k.txt" taggedAs Integration in {
	  val testDataFile = "orders4_050k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 50000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 9620
	  ordersByOrdType(Limit) shouldBe 40380

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
	"return 100 000 orders with 4 symbols for file orders4_100k.txt" taggedAs Integration in {
	  val testDataFile = "orders4_100k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 100000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 19381
	  ordersByOrdType(Limit) shouldBe 80619

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
	"return 200 000 orders with 4 symbols for file orders4_200k.txt" taggedAs Integration in {
	  val testDataFile = "orders4_200k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 200000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 38883
	  ordersByOrdType(Limit) shouldBe 161117

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
  }
  "Test data orders8_*.txt" should {
	"return 50 000 orders with 8 symbols for file orders8_050k.txt" taggedAs Integration in {
	  val testDataFile = "orders8_050k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 50000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 9849
	  ordersByOrdType(Limit) shouldBe 40151

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
	"return 100 000 orders with 8 symbols for file orders8_100k.txt" taggedAs Integration in {
	  val testDataFile = "orders8_100k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 100000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 19589
	  ordersByOrdType(Limit) shouldBe 80411

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
	"return 200 000 orders with 8 symbols for file orders8_200k.txt" taggedAs Integration in {
	  val testDataFile = "orders8_200k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 200000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 39012
	  ordersByOrdType(Limit) shouldBe 160988

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
  }
  "Test data orders12_*.txt" should {
	"return 50 000 orders with 12 symbols for file orders12_050k.txt" taggedAs Integration in {
	  val testDataFile = "orders12_050k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 50000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 9739
	  ordersByOrdType(Limit) shouldBe 40261

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
	"return 100 000 orders with 12 symbols for file orders12_100k.txt" taggedAs Integration in {
	  val testDataFile = "orders12_100k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 100000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 19572
	  ordersByOrdType(Limit) shouldBe 80428

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
	"return 200 000 orders with 12 symbols for file orders12_200k.txt" taggedAs Integration in {
	  val testDataFile = "orders12_200k.txt"
	  val messages: List[DataType] = loadData(testDataFile)

	  messages should have size 200000
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[OrderType, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(Market) shouldBe 39214
	  ordersByOrdType(Limit) shouldBe 160786

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
  }
}
