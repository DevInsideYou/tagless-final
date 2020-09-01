package com.devinsideyou
package todo
package crud

import cats._

final class BoundarySuite extends TestSuite {
  import BoundarySuite._

  private type F[A] = Id[A]

  test("description should be trimmed") {
    val entityGateway: EntityGateway[F, Unit] =
      new FakeEntityGateway[F, Unit] {
        override def writeMany(
            todos: Vector[Todo[Unit]]
          ): F[Vector[Todo.Existing[Unit]]] =
          todos.map {
            case data: Todo.Data               => Todo.Existing((), data)
            case existing: Todo.Existing[Unit] => existing
          }
      }

    val boundary: Boundary[F, Unit] =
      Boundary.dsl(entityGateway)

    forAll { data: Todo.Data =>
      boundary.createOne(data).description shouldBe data.description.trim
    }
  }

  test("readByDescription should not always call gateway.readByDescription") {
    var wasCalled = false

    val entityGateway: EntityGateway[F, Unit] =
      new FakeEntityGateway[F, Unit] {
        override def readManyByPartialDescription(
            partialDescription: String
          ): F[Vector[Todo.Existing[Unit]]] = {
          wasCalled = true

          Vector.empty
        }
      }

    val boundary: Boundary[F, Unit] =
      Boundary.dsl(entityGateway)

    When("the description is empty")
    boundary.readManyByPartialDescription("")

    Then("gateway.readByDescription should NOT be called")
    wasCalled shouldBe false

    forAll(MinSuccessful(1)) { description: String =>
      whenever(description.nonEmpty) {
        When("the description is NOT empty")
        boundary.readManyByPartialDescription(description)

        Then("gateway.readByDescription should be called")
        wasCalled shouldBe true
      }
    }
  }
}

object BoundarySuite {
  private class FakeEntityGateway[F[_], TodoId]
      extends EntityGateway[F, TodoId] {
    override def writeMany(
        todos: Vector[Todo[TodoId]]
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readManyById(
        ids: Vector[TodoId]
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readManyByPartialDescription(
        partialDescription: String
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readAll: F[Vector[Todo.Existing[TodoId]]] = ???
    override def deleteMany(todos: Vector[Todo.Existing[TodoId]]): F[Unit] = ???
    override def deleteAll: F[Unit] = ???
  }
}
