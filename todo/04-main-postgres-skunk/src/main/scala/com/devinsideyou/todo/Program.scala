package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

import cats._
import cats.implicits._

import cats.effect._

object Program {
  def dsl[F[_]: Concurrent: ContextShift: natchez.Trace]: F[Unit] =
    SessionPool.dsl.use { resource =>
      for {
        console <- Console.dsl
        random <- Random.dsl
        controller <-
          crud.DependencyGraph.dsl(Pattern, console, random, resource)
        _ <- controller.program
      } yield ()
    }

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
