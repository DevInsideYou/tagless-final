import Dependencies._
import Dependencies.io
import Util._

ThisBuild / organization := "com.devinsideyou"
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

lazy val `todo` =
  project
    .in(file("."))
    .aggregate(
      domain,
      core,
      delivery,
      persistence,
      `persistence-postgres-skunk`,
      main,
      `main-postgres-skunk`
    )

lazy val domain =
  project
    .in(file("01-domain"))
    .settings(commonSettings: _*)

lazy val core =
  project
    .in(file("02-core"))
    .dependsOn(domain % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-core`
      ),
      libraryDependencies ++= Seq(
        com.github.alexarchambault.`scalacheck-shapeless_1.14`,
        org.scalacheck.scalacheck,
        org.scalatest.scalatest,
        org.scalatestplus.`scalacheck-1-14`,
        org.typelevel.`discipline-scalatest`
      ).map(_ % Test)
    )

lazy val delivery =
  project
    .in(file("03-delivery"))
    .dependsOn(core % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-effect`
      )
    )

lazy val persistence =
  project
    .in(file("03-persistence"))
    .dependsOn(core % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-effect`
      )
    )

lazy val `persistence-postgres-skunk` =
  project
    .in(file("03-persistence-postgres-skunk"))
    .dependsOn(core % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.tpolecat.`skunk-core`,
        org.typelevel.`cats-effect`
      )
    )

lazy val main =
  project
    .in(file("04-main"))
    .dependsOn(delivery % Cctt)
    .dependsOn(persistence % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        dev.zio.zio,
        dev.zio.`zio-interop-cats`,
        io.monix.`monix-eval`
      )
    )

lazy val `main-postgres-skunk` =
  project
    .in(file("04-main-postgres-skunk"))
    .dependsOn(delivery % Cctt)
    .dependsOn(`persistence-postgres-skunk` % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        dev.zio.zio,
        dev.zio.`zio-interop-cats`,
        io.monix.`monix-eval`
      )
    )

lazy val commonSettings = Seq(
  addCompilerPlugin(com.olegpy.`better-monadic-for`),
  addCompilerPlugin(org.augustjune.`context-applied`),
  addCompilerPlugin(org.typelevel.`kind-projector`),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value
)
