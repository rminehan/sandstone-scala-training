val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "combination_parsers",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
      , "com.lihaoyi" %% "fastparse" % "3.0.1"
      , "org.scalatest" %% "scalatest" % "3.2.13" % Test
    )
  )
