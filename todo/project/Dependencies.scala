import sbt._

object Dependencies {
  case object com {
    case object github {
      case object alexarchambault {
        val `scalacheck-shapeless_1.14` =
          "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.5"
      }
    }

    case object olegpy {
      val `better-monadic-for` =
        "com.olegpy" %% "better-monadic-for" % "0.3.1"
    }
  }

  case object dev {
    case object zio {
      val zio =
        "dev.zio" %% "zio" % "1.0.3"

      val `zio-interop-cats` =
        "dev.zio" %% "zio-interop-cats" % "2.2.0.1"
    }
  }

  case object io {
    case object circe {
      val `circe-generic` =
        dependency("generic")

      private def dependency(artifact: String): ModuleID =
        "io.circe" %% s"circe-$artifact" % "0.13.0"
    }

    case object monix {
      val `monix-eval` =
        "io.monix" %% "monix-eval" % "3.3.0"
    }
  }

  case object org {
    case object augustjune {
      val `context-applied` =
        "org.augustjune" %% "context-applied" % "0.1.4"
    }

    case object http4s {
      val `http4s-blaze-server` =
        dependency("blaze-server")

      val `http4s-circe` =
        dependency("circe")

      val `http4s-dsl` =
        dependency("dsl")

      private def dependency(artifact: String): ModuleID =
        "org.http4s" %% s"http4s-$artifact" % "0.21.13"
    }

    case object scalacheck {
      val scalacheck =
        "org.scalacheck" %% "scalacheck" % "1.15.1"
    }

    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % "3.2.3"
    }

    case object scalatestplus {
      val `scalacheck-1-14` =
        "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"
    }

    case object slf4j {
      val `slf4j-simple` =
        "org.slf4j" % "slf4j-simple" % "1.7.30"
    }

    case object tpolecat {
      val `skunk-core` =
        "org.tpolecat" %% "skunk-core" % "0.0.21"
    }

    case object typelevel {
      val `cats-core` =
        "org.typelevel" %% "cats-core" % "2.3.0"

      val `cats-effect` =
        "org.typelevel" %% "cats-effect" % "2.2.0"

      val `discipline-scalatest` =
        "org.typelevel" %% "discipline-scalatest" % "2.1.0"

      val `kind-projector` =
        "org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full
    }
  }
}
