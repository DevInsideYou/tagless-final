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
        "dev.zio" %% "zio" % "1.0.0-RC21-2"

      val `zio-interop-cats` =
        "dev.zio" %% "zio-interop-cats" % "2.1.3.0-RC16"
    }
  }

  case object io {
    case object monix {
      val `monix-eval` =
        "io.monix" %% "monix-eval" % "3.2.2"
    }
  }

  case object org {
    case object augustjune {
      val `context-applied` =
        "org.augustjune" %% "context-applied" % "0.1.4"
    }

    case object scalacheck {
      val scalacheck =
        "org.scalacheck" %% "scalacheck" % "1.14.3"
    }

    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % "3.2.0"
    }

    case object scalatestplus {
      val `scalacheck-1-14` =
        "org.scalatestplus" %% "scalacheck-1-14" % "3.2.0.0"
    }

    case object tpolecat {
      val `skunk-core` =
        "org.tpolecat" %% "skunk-core" % "0.0.11"
    }

    case object typelevel {
      val `cats-core` =
        "org.typelevel" %% "cats-core" % "2.1.1"

      val `cats-effect` =
        "org.typelevel" %% "cats-effect" % "2.1.3"

      val `discipline-scalatest` =
        "org.typelevel" %% "discipline-scalatest" % "1.0.1"

      val `kind-projector` =
        "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
    }
  }
}
