package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

import cats._
import cats.syntax.all._

object DependencyGraph {
  def dsl[F[_]: effect.Sync](
      pattern: DateTimeFormatter,
      resource: effect.Resource[F, skunk.Session[F]]
    ): F[Controller[F]] =
    PostgresEntityGateway.dsl(resource).flatMap { gateway =>
      Controller.dsl(
        pattern = pattern,
        boundary = Boundary.dsl(gateway)
      )
    }
}
