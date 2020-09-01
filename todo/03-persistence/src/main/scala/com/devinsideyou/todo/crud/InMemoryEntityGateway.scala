package com.devinsideyou
package todo
package crud

import cats._
import cats.syntax.all._

import cats.effect.concurrent.Ref

object InMemoryEntityGateway {
  def dsl[F[_]: MonadError[*[_], Throwable]](
      state: Ref[F, Vector[Todo.Existing[Int]]]
    ): EntityGateway[F, Int] =
    new EntityGateway[F, Int] {
      private val statement: Statement[F, Int] =
        Statement.dsl(state)

      override def writeMany(
          todos: Vector[Todo[Int]]
        ): F[Vector[Todo.Existing[Int]]] =
        todos.traverse {
          case insert: Todo.Data          => statement.insertOne(insert)
          case update: Todo.Existing[Int] => statement.updateOne(update)
        }

      override def readManyById(
          ids: Vector[Int]
        ): F[Vector[Todo.Existing[Int]]] =
        statement
          .selectAll
          .map(_.filter(todo => ids.contains(todo.id)))

      override def readManyByPartialDescription(
          partialDescription: String
        ): F[Vector[Todo.Existing[Int]]] =
        statement
          .selectAll
          .map {
            _.filter(
              _.description
                .toLowerCase
                .contains(partialDescription.toLowerCase)
            )
          }

      override val readAll: F[Vector[Todo.Existing[Int]]] =
        statement.selectAll

      override def deleteMany(todos: Vector[Todo.Existing[Int]]): F[Unit] =
        statement.deleteMany(todos)

      override val deleteAll: F[Unit] =
        statement.deleteAll
    }
}
