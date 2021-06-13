package com.devinsideyou
package todo

import scala.concurrent._

object Main extends App {
  val executionContext: ExecutionContext =
    ExecutionContext.global

  scala.util.Random.nextInt(3) match {
    case 0 =>
      println(inColor("Running on cats.effect.IO")(scala.Console.RED))

      import cats.effect._

      implicit val cs: ContextShift[IO] =
        IO.contextShift(executionContext)

      implicit val timer: Timer[IO] =
        IO.timer(executionContext)

      Program.dsl[cats.effect.IO](executionContext).unsafeRunSync()

    case 1 =>
      println(inColor("Running on monix.eval.Task")(scala.Console.GREEN))

      import monix.execution.Scheduler.Implicits.global

      Program
        .dsl[monix.eval.Task](executionContext)
        .runSyncUnsafe(duration.Duration.Inf)

    case _ =>
      println(inColor("Running on zio.Task")(scala.Console.CYAN))

      import zio.interop.catz._
      import zio.interop.catz.implicits._

      zio.Runtime.default.unsafeRun {
        zio.Task.concurrentEffectWith { implicit concurrentEffect =>
          Program.dsl[zio.Task](executionContext)
        }
      }
  }

  private def inColor(line: String)(color: String): String =
    color + line + scala.Console.RESET
}
