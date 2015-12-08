package org.nexbook.sequence

import org.scalatest._

class SequencerTest extends FlatSpec with Matchers {

  "SequenceGenerator not initialized" should "return 1" in {
    val gen = new Sequencer

    gen.nextValue should be(1)
  }

  "SequenceGenerator with initialized value 100" should "return 101" in {
    val gen = new Sequencer(100)

    gen.nextValue should be(101)
  }
}
