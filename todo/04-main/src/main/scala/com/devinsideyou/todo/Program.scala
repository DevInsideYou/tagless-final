package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

import cats._
import cats.syntax.all._

object Program {
  def dsl[F[_]: effect.Sync]: F[Unit] =
    for {
      console <- Console.dsl
      random <- Random.dsl
      controller <- crud.DependencyGraph.dsl(Pattern, console, random)
      _ <- controller.program
    } yield ()

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
