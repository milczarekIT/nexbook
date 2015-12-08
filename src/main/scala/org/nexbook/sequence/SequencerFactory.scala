package org.nexbook.sequence

import org.nexbook.repository.OrderDatabaseRepository

/**
 * Created by milczu on 08.12.15.
 */
class SequencerFactory(orderDatabaseRepository: OrderDatabaseRepository) {
  
  val tradeIDSequencerName = "tradeIDSequencer"
  val execIDSequencerName = "execIDSequencer"

  private val (tradeIDSequencer, execIDSequencer) = initSequences

  private def initSequences = {
    val trade = new Sequencer(orderDatabaseRepository.findLastTradeID)
    val exec = new Sequencer
    (trade, exec)
  }

  def getSequencer(name: String) = name match {
    case `tradeIDSequencerName` => tradeIDSequencer
    case execIDSequencerName => execIDSequencer
  }

}
