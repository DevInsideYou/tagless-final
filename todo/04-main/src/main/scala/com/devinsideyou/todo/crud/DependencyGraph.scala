package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

import cats._

object DependencyGraph {
  def dsl[F[_]: effect.Sync](
      pattern: DateTimeFormatter,
      console: Console[F],
      random: Random[F]
    ): Controller[F] =
    Controller.dsl(
      pattern = pattern,
      boundary = Boundary.dsl(
        gateway = InMemoryEntityGateway.dsl
      ),
      console = FancyConsole.dsl(console),
      random = random
    )
}
