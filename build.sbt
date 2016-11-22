name := "finances"
version := "1.0"
scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  organization := "org.svomz",
  version := "1.0.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
    .aggregate(core, web)

lazy val core = project
    .settings(commonSettings: _*)

lazy val web = project
    .settings(commonSettings: _*)   .dependsOn(core)

    