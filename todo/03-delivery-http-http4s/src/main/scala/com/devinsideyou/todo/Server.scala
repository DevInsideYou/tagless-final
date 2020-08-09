package com.devinsideyou
package todo

import scala.concurrent._

import cats._

import org.http4s._
import org.http4s.server.blaze.BlazeServerBuilder

trait Server[F[_]] {
  def serve: F[Unit]
}

object Server {
  def dsl[F[_]: effect.ConcurrentEffect: effect.Timer](
      executionContext: ExecutionContext
    )(
      httpApp: HttpApp[F]
    ): F[Server[F]] =
    F.delay {
      new Server[F] {
        override val serve: F[Unit] =
          BlazeServerBuilder(executionContext)
            .bindHttp()
            .withHttpApp(httpApp)
            .serve
            .compile
            .drain
      }
    }
}
