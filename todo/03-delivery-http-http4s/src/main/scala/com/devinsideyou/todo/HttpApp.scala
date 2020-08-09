package com.devinsideyou
package todo

import cats._
import cats.data._
import cats.implicits._

import org.http4s._
import org.http4s.implicits._

object HttpApp {
  def dsl[F[_]: effect.Concurrent](
      routes: NonEmptyChain[HttpRoutes[F]]
    ): HttpApp[F] =
    routes
      .reduceLeft(_ <+> _)
      .orNotFound
}
