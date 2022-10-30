name := "model-play-demo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  guice,
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play27"
)
