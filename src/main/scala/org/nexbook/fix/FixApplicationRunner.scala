package org.nexbook.fix

import org.slf4j.LoggerFactory
import quickfix._

/**
  * Created by milczu on 25.08.15.
  */
trait FixApplicationRunner {

  lazy val socketAcceptorInstance = socketAcceptor
  val logger = LoggerFactory.getLogger(classOf[App])

  def run() = {
	socketAcceptorInstance.start()
	logger.info("FIX Acceptor Initialized")
  }

  protected def application: Application

  protected def logFactory: LogFactory

  protected def messageStoreFactory: MessageStoreFactory

  protected def sessionSettings: SessionSettings

  protected def socketAcceptor: SocketAcceptor = new SocketAcceptor(application, messageStoreFactory, sessionSettings, logFactory, messageFactory)

  protected def messageFactory: MessageFactory = new DefaultMessageFactory
}
