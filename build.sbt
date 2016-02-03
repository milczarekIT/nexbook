name := "nexbook"

version := "0.1"

scalaVersion := "2.11.5"

resolvers += Resolver.mavenLocal

resolvers += "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "marketcetera" at "http://repo.marketcetera.org/maven"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.5",
  "org.mockito" % "mockito-all" % "1.10.19",
  "org.apache.mina" % "mina-core" % "1.1.7",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.softwaremill.macwire" % "macros_2.11" % "1.0.6",
  "com.softwaremill.macwire" % "runtime_2.11" % "1.0.6",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12",
  "net.liftweb" % "lift-json_2.11" % "2.6.2",
  "net.liftweb" % "lift-json-ext_2.11" % "2.6.2",
  "org.mongodb" % "casbah_2.11" % "2.8.2",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "quickfixj" % "quickfixj-all" % "1.5.3"
)

testOptions in Test += Tests.Argument("-l", "org.nexbook.tags.Integration")

testOptions in Test += Tests.Argument("-l", "org.nexbook.tags.Performance")
