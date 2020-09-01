import Dependencies._

ThisBuild / organization := "com.devinsideyou"
ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Wunused:_",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

lazy val `expression-problem` =
  project
    .in(file("."))
    .settings(commonSettings)
    .aggregate(dsls, interpreters, programs, main)

lazy val dsls =
  project
    .in(file("01-dsls"))
    .settings(commonSettings)

lazy val interpreters =
  project
    .in(file("02-interpreters"))
    .dependsOn(dsls)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core" % "2.2.0"
      )
    )

lazy val programs =
  project
    .in(file("02-programs"))
    .dependsOn(dsls)
    .settings(commonSettings)

lazy val main =
  project
    .in(file("03-main"))
    .dependsOn(interpreters)
    .dependsOn(programs)
    .settings(commonSettings)

lazy val commonSettings = Seq(
  addCompilerPlugin(org.augustjune.`context-applied`),
  addCompilerPlugin(org.typelevel.`kind-projector`),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value
)
