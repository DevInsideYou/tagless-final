package com.devinsideyou
package todo
package crud

import cats._
import cats.implicits._

trait Boundary[F[_]] {
  def createOne(todo: Todo.Data): F[Todo.Existing]
  def createMany(todos: Vector[Todo.Data]): F[Vector[Todo.Existing]]

  def readOneById(id: String): F[Option[Todo.Existing]]
  def readManyById(ids: Vector[String]): F[Vector[Todo.Existing]]
  def readManyByPartialDescription(
      partialDescription: String
    ): F[Vector[Todo.Existing]]
  def readAll: F[Vector[Todo.Existing]]

  def updateOne(todo: Todo.Existing): F[Todo.Existing]
  def updateMany(todos: Vector[Todo.Existing]): F[Vector[Todo.Existing]]

  def deleteOne(todo: Todo.Existing): F[Unit]
  def deleteMany(todos: Vector[Todo.Existing]): F[Unit]
  def deleteAll: F[Unit]
}

object Boundary {
  def dsl[F[_]: Applicative](
      gateway: EntityGateway[F]
    ): Boundary[F] =
    new Boundary[F] {
      override def createOne(todo: Todo.Data): F[Todo.Existing] =
        createMany(Vector(todo)).map(_.head)

      override def createMany(
          todos: Vector[Todo.Data]
        ): F[Vector[Todo.Existing]] =
        writeMany(todos)

      private def writeMany[T <: Todo](
          todos: Vector[T]
        ): F[Vector[Todo.Existing]] =
        gateway.writeMany(
          todos.map(todo => todo.withUpdatedDescription(todo.description.trim))
        )

      override def readOneById(id: String): F[Option[Todo.Existing]] =
        readManyById(Vector(id)).map(_.headOption)

      override def readManyById(ids: Vector[String]): F[Vector[Todo.Existing]] =
        gateway.readManyById(ids)

      override def readManyByPartialDescription(
          partialDescription: String
        ): F[Vector[Todo.Existing]] =
        if (partialDescription.isEmpty)
          Vector.empty.pure[F]
        else
          gateway.readManyByPartialDescription(partialDescription.trim)

      override val readAll: F[Vector[Todo.Existing]] =
        gateway.readAll

      override def updateOne(todo: Todo.Existing): F[Todo.Existing] =
        updateMany(Vector(todo)).map(_.head)

      override def updateMany(
          todos: Vector[Todo.Existing]
        ): F[Vector[Todo.Existing]] =
        writeMany(todos)

      override def deleteOne(todo: Todo.Existing): F[Unit] =
        deleteMany(Vector(todo))

      override def deleteMany(todos: Vector[Todo.Existing]): F[Unit] =
        gateway.deleteMany(todos)

      override val deleteAll: F[Unit] =
        gateway.deleteAll
    }
}
