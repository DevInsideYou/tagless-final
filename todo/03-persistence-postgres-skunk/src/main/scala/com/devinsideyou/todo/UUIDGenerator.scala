package com.devinsideyou
package todo

import java.util.UUID

import cats._

trait UUIDGenerator[F[_]] {
  def genUUID: F[UUID]
}

object UUIDGenerator {
  implicit def dsl[F[_]: effect.Sync]: F[UUIDGenerator[F]] =
    F.delay {
      new UUIDGenerator[F] {
        override val genUUID: F[UUID] =
          F.delay(UUID.randomUUID())
      }
    }
}
