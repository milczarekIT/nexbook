package org.nexbook.fix

import org.slf4j.LoggerFactory
import quickfix._

/**
 * Created by milczu on 25.08.15.
 */
trait FixApplicationRunner {

  val logger = LoggerFactory.getLogger(classOf[App])
  lazy val socketAcceptorInstance = socketAcceptor

  protected def application: Application

  protected def messageFactory: MessageFactory = new DefaultMessageFactory

  protected def logFactory: LogFactory

  protected def messageStoreFactory: MessageStoreFactory

  protected def sessionSettings: SessionSettings

  protected def socketAcceptor: SocketAcceptor = {
    new SocketAcceptor(application, messageStoreFactory, sessionSettings, logFactory, messageFactory)
  }

  def run = {
    socketAcceptor.start
    logger.info("FIX Acceptor Initialized")
  }
}
