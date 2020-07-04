package com.devinsideyou
package todo
package crud

import cats._
import cats.implicits._

import cats.effect.concurrent.Ref

trait Statement[F[_]] {
  def insertOne(data: Todo.Data): F[Todo.Existing]
  def updateOne(todo: Todo.Existing): F[Todo.Existing]
  def selectAll: F[Vector[Todo.Existing]]
  def deleteMany(todos: Vector[Todo.Existing]): F[Unit]
  def deleteAll: F[Unit]
}

object Statement {
  def dsl[F[_]: Functor: FlatMap](
      state: Ref[F, Vector[Todo.Existing]]
    ): Statement[F] =
    new Statement[F] {
      override val selectAll: F[Vector[Todo.Existing]] =
        state.get

      private val nextId: F[String] =
        selectAll.map(_.size.toString)

      override def insertOne(data: Todo.Data): F[Todo.Existing] =
        nextId
          .map(Todo.Existing(_, data))
          .flatMap { created =>
            state.modify(s => (s :+ created) -> created)
          }

      override def updateOne(todo: Todo.Existing): F[Todo.Existing] =
        state.modify { s =>
          (s.filterNot(_.id === todo.id) :+ todo) -> todo
        }

      override def deleteMany(todos: Vector[Todo.Existing]): F[Unit] =
        state.update(_.filterNot(todo => todos.map(_.id).contains(todo.id)))

      override val deleteAll: F[Unit] =
        state.set(Vector.empty)
    }
}
