name := """one-platform-artists"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.0-rc2",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.4.0-rc2",
  "com.google.inject" % "guice" % "3.0",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.2"
)
