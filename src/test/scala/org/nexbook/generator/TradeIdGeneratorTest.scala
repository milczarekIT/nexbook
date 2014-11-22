package org.nexbook.generator

import org.scalatest.{Matchers, FlatSpec}

class TradeIdGeneratorTest extends FlatSpec with Matchers  {

  "TradeIdGenerator not initialized" should "return next AAA000001" in {
    val generator = new TradeIdGenerator()

    generator.next should be ("AAA000001")
    generator.next should be ("AAA000002")
  }


  "TradeIdGenerator initialized with AAB999999" should "return next AAC000000" in {
    val generator = new TradeIdGenerator("AAB999999")
    generator.next should be ("AAC000000")
  }

  "TradeIdGenerator initialized with AAB999999" should "return AAC000000" in {
    val generator = new TradeIdGenerator("AAZ999999")
    generator.next should be ("ABA000000")
  }

  "TradeIdGenerator initialized with AZA999999" should "return AZB000000" in {
    val generator = new TradeIdGenerator("AZA999999")
    generator.next should be ("AZB000000")
  }

}
