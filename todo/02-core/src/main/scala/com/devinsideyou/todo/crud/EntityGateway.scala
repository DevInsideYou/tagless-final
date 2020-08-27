package com.devinsideyou
package todo
package crud

trait EntityGateway[F[_], TodoId] {
  def writeMany(todos: Vector[Todo[TodoId]]): F[Vector[Todo.Existing[TodoId]]]

  def readManyById(ids: Vector[TodoId]): F[Vector[Todo.Existing[TodoId]]]
  def readManyByPartialDescription(
      partialDescription: String
    ): F[Vector[Todo.Existing[TodoId]]]
  def readAll: F[Vector[Todo.Existing[TodoId]]]

  def deleteMany(todos: Vector[Todo.Existing[TodoId]]): F[Unit]
  def deleteAll: F[Unit]
}
