import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"
)

lazy val root = (project in file("."))
  .settings(
    name := "scala-check-demo",
    libraryDependencies += munit % Test
  )
