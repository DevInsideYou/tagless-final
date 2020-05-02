package com.devinsideyou
package todo
package crud

trait Boundary {
  def createOne(todo: Todo.Data): Todo.Existing
  def createMany(todos: Vector[Todo.Data]): Vector[Todo.Existing]

  def readOneById(id: String): Option[Todo.Existing]
  def readManyById(ids: Vector[String]): Vector[Todo.Existing]
  def readManyByPartialDescription(partialDescription: String): Vector[Todo.Existing]
  def readAll: Vector[Todo.Existing]

  def updateOne(todo: Todo.Existing): Todo.Existing
  def updateMany(todos: Vector[Todo.Existing]): Vector[Todo.Existing]

  def deleteOne(todo: Todo.Existing): Unit
  def deleteMany(todos: Vector[Todo.Existing]): Unit
  def deleteAll: Unit
}

object Boundary {
  def dsl(gateway: EntityGateway): Boundary =
    new Boundary {
      override def createOne(todo: Todo.Data): Todo.Existing =
        createMany(Vector(todo)).head

      override def createMany(todos: Vector[Todo.Data]): Vector[Todo.Existing] =
        writeMany(todos)

      private def writeMany[T <: Todo](todos: Vector[T]): Vector[Todo.Existing] =
        gateway.writeMany(
          todos.map(todo => todo.withUpdatedDescription(todo.description.trim))
        )

      override def readOneById(id: String): Option[Todo.Existing] =
        readManyById(Vector(id)).headOption

      override def readManyById(ids: Vector[String]): Vector[Todo.Existing] =
        gateway.readManyById(ids)

      override def readManyByPartialDescription(partialDescription: String): Vector[Todo.Existing] =
        if (partialDescription.isEmpty)
          Vector.empty
        else
          gateway.readManyByPartialDescription(partialDescription.trim)

      override def readAll: Vector[Todo.Existing] =
        gateway.readAll

      override def updateOne(todo: Todo.Existing): Todo.Existing =
        updateMany(Vector(todo)).head

      override def updateMany(todos: Vector[Todo.Existing]): Vector[Todo.Existing] =
        writeMany(todos)

      override def deleteOne(todo: Todo.Existing): Unit =
        deleteMany(Vector(todo))

      override def deleteMany(todos: Vector[Todo.Existing]): Unit =
        gateway.deleteMany(todos)

      override def deleteAll: Unit =
        gateway.deleteAll
    }
}
