package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

import scala.concurrent._

import cats.syntax.all._

import cats.effect._

object Program {
  def dsl[F[_]: ConcurrentEffect: Timer](
      executionContext: ExecutionContext
    ): F[Unit] =
    for {
      controller <- crud.DependencyGraph.dsl(Pattern)
      server <- Server.dsl(executionContext) {
        HttpApp.dsl(
          controller
        )
      }
      _ <- server.serve
    } yield ()

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
