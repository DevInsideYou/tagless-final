package com.devinsideyou
package todo

import scala.util.chaining._

import cats._
import cats.syntax.all._

import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.http4s.server.Router

object HttpApp {
  def dsl[F[_]: effect.Concurrent](
      first: Controller[F],
      remaining: Controller[F]*
    ): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .pipe(routes => Router("api" -> routes))
      .orNotFound
      .pipe(Logger.httpApp(logHeaders = true, logBody = true))
}
