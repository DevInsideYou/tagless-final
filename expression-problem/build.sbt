import Dependencies._

ThisBuild / organization := "com.devinsideyou"
ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  // "-Wunused:_",
  // "-Xfatal-warnings",
  "-Ymacro-annotations"
)

lazy val `expression-problem` =
  project
    .in(file("."))
    .settings(commonSettings: _*)
    .aggregate(dsls, interpreters, programs, main)

lazy val dsls =
  project
    .in(file("01-dsls"))
    .settings(commonSettings: _*)

lazy val interpreters =
  project
    .in(file("02-interpreters"))
    .dependsOn(dsls)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core" % "2.1.1"
      )
    )

lazy val programs =
  project
    .in(file("02-programs"))
    .dependsOn(dsls)
    .settings(commonSettings: _*)

lazy val main =
  project
    .in(file("03-main"))
    .dependsOn(interpreters)
    .dependsOn(programs)
    .settings(commonSettings: _*)

lazy val commonSettings = Seq(
  addCompilerPlugin(org.typelevel.`kind-projector`),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value
)
