name := """authenticator-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.h2database" % "h2" % "1.4.200",
  "ch.qos.logback" % "logback-classic" % "0.9.28" % "test",
  "org.postgresql" % "postgresql" % "42.5.1",
  filters,
)
