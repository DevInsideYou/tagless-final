import Dependencies.{ io, _ }
import Util._

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

lazy val `todo` =
  project
    .in(file("."))
    .aggregate(
      util,
      domain,
      core,
      delivery,
      `delivery-http-http4s`,
      persistence,
      `persistence-postgres-skunk`,
      main,
      `main-http-http4s`,
      `main-postgres-skunk`,
      `main-http-http4s-postgres-skunk`
    )

lazy val util =
  project
    .in(file("00-util"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-core`
      )
    )

lazy val domain =
  project
    .in(file("01-domain"))
    .dependsOn(util % Cctt)
    .settings(commonSettings)

lazy val core =
  project
    .in(file("02-core"))
    .dependsOn(domain % Cctt)
    .settings(commonSettings)
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
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-effect`
      )
    )

lazy val `delivery-http-http4s` =
  project
    .in(file("03-delivery-http-http4s"))
    .dependsOn(core % Cctt)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        io.circe.`circe-generic`,
        org.http4s.`http4s-blaze-server`,
        org.http4s.`http4s-circe`,
        org.http4s.`http4s-dsl`,
        org.typelevel.`cats-effect`
      )
    )

lazy val persistence =
  project
    .in(file("03-persistence"))
    .dependsOn(core % Cctt)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-effect`
      )
    )

lazy val `persistence-postgres-skunk` =
  project
    .in(file("03-persistence-postgres-skunk"))
    .dependsOn(core % Cctt)
    .settings(commonSettings)
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
    .settings(commonSettings)
    .settings(libraryDependencies ++= effects)

lazy val `main-http-http4s` =
  project
    .in(file("04-main-http-http4s"))
    .dependsOn(`delivery-http-http4s` % Cctt)
    .dependsOn(persistence % Cctt)
    .settings(commonSettings)
    .settings(libraryDependencies ++= effects)
    .settings(
      libraryDependencies ++= Seq(
        org.slf4j.`slf4j-simple`
      )
    )

lazy val `main-postgres-skunk` =
  project
    .in(file("04-main-postgres-skunk"))
    .dependsOn(delivery % Cctt)
    .dependsOn(`persistence-postgres-skunk` % Cctt)
    .settings(commonSettings)
    .settings(libraryDependencies ++= effects)

lazy val `main-http-http4s-postgres-skunk` =
  project
    .in(file("04-main-http-http4s-postgres-skunk"))
    .dependsOn(`delivery-http-http4s` % Cctt)
    .dependsOn(`persistence-postgres-skunk` % Cctt)
    .settings(commonSettings)
    .settings(libraryDependencies ++= effects)
    .settings(
      libraryDependencies ++= Seq(
        org.slf4j.`slf4j-simple`
      )
    )

lazy val commonSettings = Seq(
  addCompilerPlugin(com.olegpy.`better-monadic-for`),
  addCompilerPlugin(org.augustjune.`context-applied`),
  addCompilerPlugin(org.typelevel.`kind-projector`),
  update / evictionWarningOptions := EvictionWarningOptions.empty,
  libraryDependencies ++= Seq(
    com.github.alexarchambault.`scalacheck-shapeless_1.14`,
    org.scalacheck.scalacheck,
    org.scalatest.scalatest,
    org.scalatestplus.`scalacheck-1-14`,
    org.typelevel.`discipline-scalatest`
  ).map(_ % Test),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value
)

lazy val effects = Seq(
  dev.zio.`zio-interop-cats`,
  dev.zio.zio,
  io.monix.`monix-eval`,
  org.typelevel.`cats-effect`
)
