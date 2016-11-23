name := """finances-web"""
scalaVersion := "2.11.8"

lazy val web = (project in file("."))
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

