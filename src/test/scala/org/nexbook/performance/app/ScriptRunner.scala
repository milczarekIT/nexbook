package org.nexbook.performance.app

import org.slf4j.LoggerFactory

import scala.tools.nsc.ScriptRunner

/**
  * Created by milczu on 1/2/16.
  */
object ScriptRunner {

  val logger = LoggerFactory.getLogger(classOf[ScriptRunner])

  import sys.process._

  val appPath = execCommand("pwd")
  val scriptsDir = "src/test/resources/scripts"

  def executeScript(script: String): String = executeScripts(Seq(script))

  def executeScripts(scripts: Seq[String]): String = {
	val sb = new StringBuilder
	scripts.foreach {
	  s => {
		logger.info(s"execute script $s")
		val output: String = (stringSeqToProcess(Seq("bash", s"$appPath/$scriptsDir/$s")) !!).trim
		if(!output.isEmpty) {
		  sb.append(output)
		}
	  }
	}
	sb.toString()
  }


  def execCommand(command: String): String = (command !!).trim
}
