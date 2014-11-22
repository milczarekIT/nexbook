package org.nexbook.generator

import org.scalatest._

class SequenceGeneratorTest extends FlatSpec with  Matchers {

  "SequenceGenerator not initialized" should "return 1" in {
    val gen = new SequenceGenerator

    gen.nextValue should be (1)
  }

  "SequenceGenerator with initialized value 100" should "return 101" in {
    val gen = new SequenceGenerator(100)

    gen.nextValue should be (101)
  }
}
