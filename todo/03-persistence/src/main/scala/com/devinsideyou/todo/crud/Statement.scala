package com.devinsideyou
package todo
package crud

import cats._
import cats.implicits._

import cats.effect.concurrent.Ref

trait Statement[F[_]] {
  def insertOne(data: Todo.Data): F[Todo.Existing[Int]]
  def updateOne(todo: Todo.Existing[Int]): F[Todo.Existing[Int]]
  def selectAll: F[Vector[Todo.Existing[Int]]]
  def deleteMany(todos: Vector[Todo.Existing[Int]]): F[Unit]
  def deleteAll: F[Unit]
}

object Statement {
  def dsl[F[_]: MonadError[*[_], Throwable]](
      state: Ref[F, Vector[Todo.Existing[Int]]]
    ): Statement[F] =
    new Statement[F] {
      override val selectAll: F[Vector[Todo.Existing[Int]]] =
        state.get

      private val nextId: F[Int] =
        selectAll.map(_.size)

      override def insertOne(data: Todo.Data): F[Todo.Existing[Int]] =
        nextId
          .map(Todo.Existing(_, data))
          .flatMap { created =>
            state.modify(s => (s :+ created) -> created)
          }

      override def updateOne(todo: Todo.Existing[Int]): F[Todo.Existing[Int]] =
        state.get.flatMap { s =>
          if (s.exists(_.id === todo.id))
            state.modify { s =>
              (s.filterNot(_.id === todo.id) :+ todo) -> todo
            }
          else
            F.raiseError(
              new RuntimeException(
                s"Failed to update todo: ${todo.id} because it didn't exist."
              )
            )
        }

      override def deleteMany(todos: Vector[Todo.Existing[Int]]): F[Unit] =
        state.update(_.filterNot(todo => todos.map(_.id).contains(todo.id)))

      override val deleteAll: F[Unit] =
        state.set(Vector.empty)
    }
}
