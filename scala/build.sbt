name := "yougi"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings
