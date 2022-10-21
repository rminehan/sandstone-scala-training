name := "model-play-demo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  "org.typelevel" %% "cats-core" % "2.7.0"
)
