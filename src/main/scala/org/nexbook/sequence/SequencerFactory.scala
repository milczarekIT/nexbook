package org.nexbook.sequence

import org.nexbook.repository.{ExecutionDatabaseRepository, OrderDatabaseRepository}

/**
  * Created by milczu on 08.12.15.
  */
class SequencerFactory(orderDatabaseRepository: OrderDatabaseRepository, executionDatabaseRepository: ExecutionDatabaseRepository) {

  private val (tradeIDSequencer, execIDSequencer) = initSequences

  def sequencer(name: String) = name match {
	case SequencerFactory.tradeIDSequencerName => tradeIDSequencer
	case SequencerFactory.execIDSequencerName => execIDSequencer
  }

  private def initSequences = {
	val trade = new Sequencer(orderDatabaseRepository.findLastTradeID)
	val exec = new Sequencer(executionDatabaseRepository.findLastExecID)
	(trade, exec)
  }
}

object SequencerFactory {
  val tradeIDSequencerName = "tradeIDSequencer"
  val execIDSequencerName = "execIDSequencer"
}
