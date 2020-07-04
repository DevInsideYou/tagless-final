package com.devinsideyou
package todo
package crud

import cats._
import cats.implicits._

trait Statement[F[_]] {
  def insertOne(data: Todo.Data): F[Todo.Existing]
  def updateOne(todo: Todo.Existing): F[Todo.Existing]
  def selectAll: F[Vector[Todo.Existing]]
  def deleteMany(todos: Vector[Todo.Existing]): F[Unit]
  def deleteAll: F[Unit]
}

object Statement {
  def dsl[F[_]: effect.Sync]: Statement[F] =
    new Statement[F] {
      var state: Vector[Todo.Existing] = Vector.empty

      override val selectAll: F[Vector[Todo.Existing]] =
        F.delay(state)

      private val nextId: F[String] =
        F.delay(state.size.toString)

      override def insertOne(data: Todo.Data): F[Todo.Existing] =
        nextId
          .map(Todo.Existing(_, data))
          .flatMap { created =>
            F.delay(state :+= created).as(created)
          }

      override def updateOne(todo: Todo.Existing): F[Todo.Existing] =
        F.delay {
          state = state.filterNot(_.id === todo.id) :+ todo
        }.as(todo)

      override def deleteMany(todos: Vector[Todo.Existing]): F[Unit] =
        F.delay {
          state = state.filterNot(todo => todos.map(_.id).contains(todo.id))
        }

      override val deleteAll: F[Unit] =
        F.delay {
          state = Vector.empty
        }
    }
}
