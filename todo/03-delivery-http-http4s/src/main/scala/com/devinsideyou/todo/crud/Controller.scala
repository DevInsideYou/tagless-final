package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

import scala.util.chaining._

import cats._
import cats.implicits._

import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

trait Controller[F[_]] {
  def routes: HttpRoutes[F]
}

object Controller {
  def dsl[F[_]: Defer: Monad](
      pattern: DateTimeFormatter,
      boundary: Boundary[F]
    ): Controller[F] =
    new Controller[F] with Http4sDsl[F] {
      override val routes: HttpRoutes[F] =
        HttpRoutes.of {
          case GET -> Root / "todos" => showAll
        }

      private val showAll: F[Response[F]] =
        boundary.readAll.flatMap { todos =>
          todos
            .sortBy(_.deadline)
            .map(response.Todo(pattern))
            .asJson
            .pipe(Ok(_))
        }
    }
}
