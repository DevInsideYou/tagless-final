package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter.{ ISO_LOCAL_DATE_TIME => Pattern }

import cats._
import cats.effect.concurrent.Ref

final class ControllerSuite extends TestSuite {
  import ControllerSuite._

  private type F[+A] = effect.IO[A]
  private val F = effect.IO

  test("test suite should quit automatically") {
    val boundary: Boundary[F, Unit] =
      new FakeBoundary[F, Unit]

    assert(
      boundary,
      input = List.empty,
      expectedOutput = Vector.empty
    )
  }

  test("should create on 'c'") {
    val boundary: Boundary[F, Unit] =
      new FakeBoundary[F, Unit] {
        override def createOne(todo: Todo.Data): F[Todo.Existing[Unit]] =
          F.pure(Todo.Existing((), todo))
      }

    assert(
      boundary,
      input = List("c", "Invent time-travel!", "1955-11-5 18:00"),
      expectedOutput = Vector("Successfully created the new todo.")
    )
  }

  test("should keep running on error") {
    val boundary: Boundary[F, Unit] =
      new FakeBoundary[F, Unit] {
        override def createOne(todo: Todo.Data): F[Todo.Existing[Unit]] =
          F.raiseError(new RuntimeException("boom"))
      }

    forAll { description: String =>
      assert(
        boundary,
        input = List("c", description, "1955-11-5 18:00"),
        expectedOutput = Vector.empty,
        expectedErrors = Vector("boom")
      )
    }
  }

  test("should yield an error if deadline does not match the required format") {
    val boundary: Boundary[F, Unit] =
      new FakeBoundary[F, Unit]

    forAll { (description: String, deadline: String) =>
      import scala.Console._

      assert(
        boundary,
        input = List("c", description, deadline),
        expectedOutput = Vector.empty,
        expectedErrors = Vector(
          s"\n${YELLOW}${deadline.trim}${RESET} does not match the required format ${MAGENTA}yyyy-M-d H:m${RESET}."
        )
      )
    }
  }

  private def assert[TodoId](
      boundary: Boundary[F, TodoId],
      input: List[String],
      expectedOutput: Vector[String],
      expectedErrors: Vector[String] = Vector.empty
    )(implicit
      parse: Parse[String, TodoId]
    ): Assertion = {
    val program: F[Assertion] =
      for {
        ref <- Ref.of[F, UsefulConsole.State](UsefulConsole.State(input :+ "q"))
        controller = Controller.dsl[F, TodoId](
          Pattern,
          boundary,
          new UsefulConsole(ref),
          new UsefulRandom(fakeN = 5)
        )
        _ <- controller.program
        data <- ref.get
      } yield {
        data.output shouldBe (expectedOutput :+ "\nUntil next time!\n")
        data.errors shouldBe expectedErrors
      }

    program.unsafeRunSync()
  }
}

object ControllerSuite {
  private class FakeBoundary[F[_], TodoId] extends Boundary[F, TodoId] {
    override def createOne(todo: Todo.Data): F[Todo.Existing[TodoId]] = ???

    override def createMany(
        todos: Vector[Todo.Data]
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readOneById(id: TodoId): F[Option[Todo.Existing[TodoId]]] = ???

    override def readManyById(
        ids: Vector[TodoId]
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readManyByPartialDescription(
        partialDescription: String
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def readAll: F[Vector[Todo.Existing[TodoId]]] = ???

    override def updateOne(
        todo: Todo.Existing[TodoId]
      ): F[Todo.Existing[TodoId]] = ???

    override def updateMany(
        todos: Vector[Todo.Existing[TodoId]]
      ): F[Vector[Todo.Existing[TodoId]]] = ???

    override def deleteOne(todo: Todo.Existing[TodoId]): F[Unit] = ???
    override def deleteMany(todos: Vector[Todo.Existing[TodoId]]): F[Unit] = ???
    override def deleteAll: F[Unit] = ???
  }

  private class FakeFancyConsole[F[_]] extends FancyConsole[F] {
    override def getStrLnTrimmedWithPrompt(prompt: String): F[String] = ???
    override def putStrLn(line: String): F[Unit] = ???
    override def putSuccess(line: String): F[Unit] = ???
    override def putWarning(line: String): F[Unit] = ???
    override def putErrLn(line: String): F[Unit] = ???
    override def putStrLnInColor(line: String)(color: String): F[Unit] = ???
  }

  private class FakeRandom[F[_]] extends Random[F] {
    override def nextInt(n: Int): F[Int] = ???
  }

  implicit private val parseStringToUnit: Parse[String, Unit] =
    _ => Right(())

  private class UsefulRandom[F[_]: Applicative](fakeN: Int)
      extends FakeRandom[F] {
    override def nextInt(n: Int): F[Int] =
      F.pure(fakeN)
  }

  private class UsefulConsole[F[_]: effect.Sync](
      ref: Ref[F, UsefulConsole.State]
    ) extends FakeFancyConsole[F] {
    override def getStrLnTrimmedWithPrompt(prompt: String): F[String] =
      ref.modify { state =>
        val head :: tail = state.input

        state.copy(input = tail) -> head
      }

    override def putStrLn(line: String): F[Unit] =
      ref.update { state =>
        state.copy(output = state.output :+ line)
      }

    override def putSuccess(line: String): F[Unit] =
      putStrLn(line)

    override def putErrLn(line: String): F[Unit] =
      ref.update { state =>
        state.copy(errors = state.errors :+ line)
      }
  }

  object UsefulConsole {
    final case class State(
        input: List[String],
        output: Vector[String] = Vector.empty,
        errors: Vector[String] = Vector.empty
      )
  }
}
