package org.nexbook.fix

import org.nexbook.app.ApplicationRunner
import org.slf4j.LoggerFactory
import quickfix._

/**
  * Created by milczu on 25.08.15.
  */
trait FixApplicationRunner extends ApplicationRunner {

  lazy val socketAcceptorInstance = socketAcceptor
  val logger = LoggerFactory.getLogger(classOf[App])

  override def start(): Unit = {
	socketAcceptorInstance.start()
	logger.info("FIX Acceptor Initialized")
  }

  override def stop(): Unit = {
	socketAcceptorInstance.stop(true)
	logger.info("FIX Acceptor Stopped")
  }

  protected def application: Application

  protected def logFactory: LogFactory

  protected def messageStoreFactory: MessageStoreFactory

  protected def sessionSettings: SessionSettings

  protected def socketAcceptor: SocketAcceptor = new SocketAcceptor(application, messageStoreFactory, sessionSettings, logFactory, messageFactory)

  protected def messageFactory: MessageFactory = new DefaultMessageFactory
}
