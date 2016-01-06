package org.nexbook.performance.app

/**
  * Created by milczu on 1/2/16.
  */
object ScriptRunner {

  import sys.process._

  val appPath = execCommand("pwd")
  val scriptsDir = "src/test/resources/scripts"

  def executeScript(script: String): String = executeScripts(Seq(script))

  def executeScripts(scripts: Seq[String]): String = {
	val sb = new StringBuilder
	scripts.foreach {
	  s => {
		val output: String = (stringSeqToProcess(Seq("bash", s"$appPath/$scriptsDir/$s")) !!).trim
		if (!output.isEmpty) {
		  sb.append(output)
		}
	  }
	}
	sb.toString()
  }


  def execCommand(command: String): String = (command !!).trim
}
