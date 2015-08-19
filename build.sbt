name := "nexbook"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.5",
  "org.apache.mina" % "mina-core" % "1.1.7",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "com.softwaremill.macwire" % "macros" % "1.0.6",
  "com.softwaremill.macwire" % "runtime" % "1.0.6"
)