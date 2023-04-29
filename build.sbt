name := "Akka-Assingment"

version := "0.1"

scalaVersion := "2.13.10"
val akkaVersion = "2.8.0"
val scalaTestVersion = "3.2.15"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
)
