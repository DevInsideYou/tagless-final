package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

import scala.concurrent._

import cats.syntax.all._

import cats.effect._

object Program {
  def dsl[F[_]: ConcurrentEffect: ContextShift: Timer: natchez.Trace](
      executionContext: ExecutionContext
    ): F[Unit] =
    SessionPool.dsl.use { resource =>
      for {
        controller <- crud.DependencyGraph.dsl(Pattern, resource)
        server <- Server.dsl(executionContext) {
          HttpApp.dsl(
            controller
          )
        }
        _ <- server.serve
      } yield ()
    }

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
