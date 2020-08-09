package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

import scala.util.chaining._

import cats._
import cats.data._
import cats.implicits._

import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

object Controller {
  def dsl[F[_]: effect.Sync](
      pattern: DateTimeFormatter,
      boundary: Boundary[F]
    ): F[Controller[F]] =
    F.delay {
      new Controller[F] with Http4sDsl[F] {
        override val routes: HttpRoutes[F] =
          Router {
            "todos" -> HttpRoutes.of {
              case r @ POST -> Root =>
                r.as[request.Todo.Create].flatMap(create)

              case r @ PUT -> Root / id =>
                r.as[request.Todo.Update].flatMap(update(id))

              case GET -> Root :? Description(d) => searchByDescription(d)
              case GET -> Root                   => showAll
              case GET -> Root / id              => searchById(id)

              case DELETE -> Root      => deleteAll
              case DELETE -> Root / id => delete(id)
            }
          }

        object Description
            extends QueryParamDecoderMatcher[String]("description")

        private def create(payload: request.Todo.Create): F[Response[F]] =
          withDeadlinePrompt(payload.deadline) { deadline =>
            boundary
              .createOne(Todo.Data(payload.description, deadline))
              .map(response.Todo(pattern))
              .map(_.asJson)
              .flatMap(Created(_))
          }

        private def withDeadlinePrompt(
            deadline: String
          )(
            onSuccess: LocalDateTime => F[Response[F]]
          ): F[Response[F]] =
          deadline.pipe(toLocalDateTime) match {
            case Right(deadline) => onSuccess(deadline)
            case Left(error)     => BadRequest(error)
          }

        private def toLocalDateTime(
            input: String
          ): Either[String, LocalDateTime] = {
          val formatter =
            DateTimeFormatter
              .ofPattern(DeadlinePromptPattern)

          val trimmedInput: String =
            input.trim

          Either
            .catchNonFatal(LocalDateTime.parse(trimmedInput, formatter))
            .leftMap { _ =>
              s"$trimmedInput does not match the required format $DeadlinePromptPattern"
            }
        }

        private def update(
            id: String
          ): request.Todo.Update => F[Response[F]] = {
          case payload: request.Todo.Update.Description =>
            updateDescription(id, payload)

          case payload: request.Todo.Update.Deadline =>
            updateDeadline(id, payload)

          case payload: request.Todo.Update.AllFields =>
            updateAllFields(id, payload)
        }

        private def updateDescription(
            id: String,
            payload: request.Todo.Update.Description
          ): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { todo =>
              boundary
                .updateOne(todo.withUpdatedDescription(payload.description))
                .map(response.Todo(pattern))
                .map(_.asJson)
                .flatMap(Ok(_))
            }
          }

        private def updateDeadline(
            id: String,
            payload: request.Todo.Update.Deadline
          ): F[Response[F]] =
          withIdPrompt(id) { id =>
            withDeadlinePrompt(payload.deadline) { deadline =>
              withReadOne(id) { todo =>
                boundary
                  .updateOne(todo.withUpdatedDeadline(deadline))
                  .map(response.Todo(pattern))
                  .map(_.asJson)
                  .flatMap(Ok(_))
              }
            }
          }

        private def updateAllFields(
            id: String,
            payload: request.Todo.Update.AllFields
          ): F[Response[F]] =
          withIdPrompt(id) { id =>
            withDeadlinePrompt(payload.deadline) { deadline =>
              withReadOne(id) { todo =>
                boundary
                  .updateOne(
                    todo
                      .withUpdatedDescription(payload.description)
                      .withUpdatedDeadline(deadline)
                  )
                  .map(response.Todo(pattern))
                  .map(_.asJson)
                  .flatMap(Ok(_))
              }
            }
          }

        private val showAll: F[Response[F]] =
          boundary.readAll.flatMap { todos =>
            todos
              .sortBy(_.deadline)
              .map(response.Todo(pattern))
              .asJson
              .pipe(Ok(_))
          }

        private def searchById(id: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { todo =>
              todo
                .pipe(response.Todo(pattern))
                .pipe(_.asJson)
                .pipe(Ok(_))
            }
          }

        private def searchByDescription(description: String): F[Response[F]] =
          boundary.readManyByPartialDescription(description).flatMap { todos =>
            todos
              .map(response.Todo(pattern))
              .asJson
              .pipe(Ok(_))
          }

        private def delete(id: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { todo =>
              boundary.deleteOne(todo) >>
                NoContent()
            }
          }

        private def withIdPrompt(
            id: String
          )(
            onValidId: String => F[Response[F]]
          ): F[Response[F]] =
          id.pipe(toId) match {
            case Right(id)   => onValidId(id)
            case Left(error) => BadRequest(error)
          }

        private def toId(userInput: String): Either[String, String] =
          if (userInput.isEmpty || userInput.contains(" "))
            Left(s"$userInput is not a valid id.")
          else
            Right(userInput)

        private def withReadOne(
            id: String
          )(
            onFound: Todo.Existing => F[Response[F]]
          ): F[Response[F]] =
          boundary
            .readOneById(id)
            .flatMap {
              case Some(todo) => onFound(todo)
              case None       => displayNoTodosFoundMessage
            }

        private val displayNoTodosFoundMessage: F[Response[F]] =
          NotFound("No todos found!")

        private val deleteAll: F[Response[F]] =
          boundary.deleteAll >> NoContent()
      }
    }

  object request {
    object Todo {
      final case class Create(description: String, deadline: String)
          extends Update

      object Create {
        implicit val decoder: Decoder[Create] =
          deriveDecoder

        implicit def entityDecoder[
            F[_]: effect.Sync
          ]: EntityDecoder[F, Create] =
          jsonOf
      }

      sealed abstract class Update extends Product with Serializable

      object Update {
        final case class Description(description: String) extends Update
        final case class Deadline(deadline: String) extends Update
        final type AllFields = Create

        implicit val decoder: Decoder[Update] =
          NonEmptyChain[Decoder[Update]](
            deriveDecoder[AllFields].widen, // order matters
            deriveDecoder[Description].widen,
            deriveDecoder[Deadline].widen
          ).reduceLeft(_ or _)

        implicit def entityDecoder[
            F[_]: effect.Sync
          ]: EntityDecoder[F, Update] =
          jsonOf
      }
    }
  }

  private val DeadlinePromptPattern: String =
    "yyyy-M-d H:m"
}
