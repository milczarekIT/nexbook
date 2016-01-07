package org.nexbook.testutils

import org.nexbook.tags.Integration
import org.scalatest.{Matchers, WordSpecLike}
import quickfix.Message
import quickfix.field.OrdType
import quickfix.fix44.NewOrderSingle

/**
  * Created by milczu on 07.01.16.
  */
class FixMessageProviderTest extends WordSpecLike with Matchers {

  type DataType = Message
  
  val testDataDir = "src/test/resources/data"
  val instanceOfNewOrderSingle: DataType => Boolean = m => m.isInstanceOf[NewOrderSingle]
  val marketOrLimitOrder: DataType => Boolean = m => {
	val order = m.asInstanceOf[NewOrderSingle]
	order.getOrdType.getValue match {
	  case OrdType.LIMIT | OrdType.MARKET => true
	  case _ => false
	}
  }

  val ordTypeExtractor: DataType => Char = m => m.asInstanceOf[NewOrderSingle].getOrdType.getValue
  val symbolExtractor: DataType => String = m => m.asInstanceOf[NewOrderSingle].getSymbol.getValue

  val loadData: String => List[DataType] = fileName => FixMessageProvider.get(fileName).map(_._1)

  "Test data orders4_*.txt" should {
	"return 50 000 orders with 4 symbols for file orders4_050k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders4_050k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 50000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 9620
	  ordersByOrdType(OrdType.LIMIT) shouldBe 40380

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
	"return 100 000 orders with 4 symbols for file orders4_100k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders4_100k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 100000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 19381
	  ordersByOrdType(OrdType.LIMIT) shouldBe 80619

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
	"return 200 000 orders with 4 symbols for file orders4_200k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders4_200k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 200000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 38883
	  ordersByOrdType(OrdType.LIMIT) shouldBe 161117

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 4
	}
  }
  "Test data orders8_*.txt" should {
	"return 50 000 orders with 8 symbols for file orders8_050k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders8_050k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 50000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 9849
	  ordersByOrdType(OrdType.LIMIT) shouldBe 40151

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
	"return 100 000 orders with 8 symbols for file orders8_100k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders8_100k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 100000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 19589
	  ordersByOrdType(OrdType.LIMIT) shouldBe 80411

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
	"return 200 000 orders with 8 symbols for file orders8_200k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders8_200k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 200000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 39012
	  ordersByOrdType(OrdType.LIMIT) shouldBe 160988

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 8
	}
  }
  "Test data orders12_*.txt" should {
	"return 50 000 orders with 12 symbols for file orders12_050k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders12_050k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 50000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 9739
	  ordersByOrdType(OrdType.LIMIT) shouldBe 40261

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
	"return 100 000 orders with 12 symbols for file orders12_100k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders12_100k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 100000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 19572
	  ordersByOrdType(OrdType.LIMIT) shouldBe 80428

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
	"return 200 000 orders with 12 symbols for file orders12_200k.txt" taggedAs Integration in {
	  val testDataPath = s"$testDataDir/orders12_200k.txt"
	  val messages: List[DataType] = loadData(testDataPath)

	  messages should have size 200000
	  messages forall instanceOfNewOrderSingle shouldBe true
	  messages forall marketOrLimitOrder shouldBe true

	  val ordersByOrdType: Map[Char, Int] = messages.groupBy(ordTypeExtractor).map(e => e._1 -> e._2.size)
	  ordersByOrdType(OrdType.MARKET) shouldBe 39214
	  ordersByOrdType(OrdType.LIMIT) shouldBe 160786

	  val symbols = messages.groupBy(symbolExtractor).keySet
	  symbols should have size 12
	}
  }
}
