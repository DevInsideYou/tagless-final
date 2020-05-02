package com.devinsideyou
package todo
package crud

object InMemoryEntityGateway {
  val dsl: EntityGateway = {
    var nextId: Int = 0
    var state: Vector[Todo.Existing] = Vector.empty

    new EntityGateway {
      override def writeMany(todos: Vector[Todo]): Vector[Todo.Existing] =
        todos.map(writeOne)

      private def writeOne(todo: Todo): Todo.Existing =
        todo match {
          case item: Todo.Data     => createOne(item)
          case item: Todo.Existing => updateOne(item)
        }

      private def createOne(todo: Todo.Data): Todo.Existing = {
        val created =
          Todo.Existing(
            id = nextId.toString,
            data = todo
          )

        state :+= created

        nextId += 1

        created
      }

      override def readManyById(ids: Vector[String]): Vector[Todo.Existing] =
        state.filter(todo => ids.contains(todo.id))

      override def readManyByPartialDescription(partialDescription: String): Vector[Todo.Existing] =
        state.filter(
          _.description
            .toLowerCase
            .contains(partialDescription.toLowerCase)
        )

      override def readAll: Vector[Todo.Existing] =
        state

      private def updateOne(todo: Todo.Existing): Todo.Existing = {
        state = state.filterNot(_.id == todo.id) :+ todo

        todo
      }

      override def deleteMany(todos: Vector[Todo.Existing]): Unit =
        state = state.filterNot(todo => todos.map(_.id).contains(todo.id))

      override def deleteAll: Unit =
        state = Vector.empty
    }
  }
}
