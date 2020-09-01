package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

import cats._
import cats.syntax.all._

import cats.effect.concurrent.Ref

object DependencyGraph {
  def dsl[F[_]: effect.Sync](
      pattern: DateTimeFormatter,
      console: Console[F],
      random: Random[F]
    ): F[Controller[F]] =
    Ref.of(Vector.empty[Todo.Existing[Int]]).map { state =>
      Controller.dsl(
        pattern = pattern,
        boundary = Boundary.dsl(
          gateway = InMemoryEntityGateway.dsl(state)
        ),
        console = FancyConsole.dsl(console),
        random = random
      )
    }
}
