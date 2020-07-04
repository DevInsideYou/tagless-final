package com.devinsideyou
package todo
package crud

import cats._
import cats.implicits._

object InMemoryEntityGateway {
  def dsl[F[_]: effect.Sync]: EntityGateway[F] =
    new EntityGateway[F] {
      val statement: Statement[F] =
        Statement.dsl

      override def writeMany(todos: Vector[Todo]): F[Vector[Todo.Existing]] =
        todos.traverse {
          case insert: Todo.Data     => statement.insertOne(insert)
          case update: Todo.Existing => statement.updateOne(update)
        }

      override def readManyById(
          ids: Vector[String]
        ): F[Vector[Todo.Existing]] =
        statement
          .selectAll
          .map(_.filter(todo => ids.contains(todo.id)))

      override def readManyByPartialDescription(
          partialDescription: String
        ): F[Vector[Todo.Existing]] =
        statement
          .selectAll
          .map {
            _.filter(
              _.description
                .toLowerCase
                .contains(partialDescription.toLowerCase)
            )
          }

      override val readAll: F[Vector[Todo.Existing]] =
        statement.selectAll

      override def deleteMany(todos: Vector[Todo.Existing]): F[Unit] =
        statement.deleteMany(todos)

      override val deleteAll: F[Unit] =
        statement.deleteAll
    }
}
