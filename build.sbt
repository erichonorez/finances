import sbt.Keys._

name := "finances"
scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  organization := "org.svomz",
  version := "1.0.0",
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "7.2.6"
  )
)

lazy val root = (project in file("."))
    .settings(commonSettings)
    .aggregate(core, web)

lazy val core = project
    .settings(commonSettings: _*)
    .settings(
        libraryDependencies ++= Seq(
          "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
          "junit" % "junit" % "4.10" % "test",
          "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"
        )
    )

lazy val web = project
    .settings(commonSettings: _*)
    .dependsOn(core)
    .enablePlugins(PlayScala)
    .settings(
      libraryDependencies ++= Seq(
        jdbc,
        cache,
        ws,
        "net.codingwell" %% "scala-guice" % "4.1.0",
        "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1",
        "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
      )
    )