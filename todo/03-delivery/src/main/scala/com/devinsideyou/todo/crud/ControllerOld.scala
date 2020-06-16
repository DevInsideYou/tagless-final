package com.devinsideyou
package todo
package crud

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

trait ControllerOld {
  def run(): Unit
}

object ControllerOld {
  def dsl(
      boundary: BoundaryOld,
      pattern: DateTimeFormatter
    )(implicit
      fancyConsole: FancyConsoleOld,
      random: RandomOld
    ): ControllerOld =
    new ControllerOld {
      override def run(): Unit = {
        val colors: Vector[String] =
          Vector(
            // scala.Console.BLACK,
            scala.Console.BLUE,
            scala.Console.CYAN,
            scala.Console.GREEN,
            scala.Console.MAGENTA,
            scala.Console.RED,
            // scala.Console.WHITE,
            scala.Console.YELLOW
          )

        def randomColor: String =
          random.nextInt(colors.size).pipe(colors)

        def hyphens: String =
          randomColor.pipe(inColor("─" * 100))

        def menu: String =
          s"""|
              |$hyphens
              |
              |c                   => create new todo
              |d                   => delete todo
              |da                  => delete all todos
              |sa                  => show all todos
              |sd                  => search by partial description
              |sid                 => search by id
              |ud                  => update description
              |udl                 => update deadline
              |e | q | exit | quit => exit the application
              |anything else       => show the main menu
              |
              |Please enter a command:""".stripMargin

        def prompt: String =
          menu.pipe(fancyConsole.getStrLnTrimmedWithPrompt)

        object Exit {
          def unapply(s: String): Boolean =
            Set("e", "q", "exit", "quit")(s)
        }

        @scala.annotation.tailrec
        def loop(shouldKeepLooping: Boolean): Unit =
          if (shouldKeepLooping)
            loop {
              prompt match {
                case "c"    => create(); true
                case "d"    => delete(); true
                case "da"   => deleteAll(); true
                case "sa"   => showAll(); true
                case "sd"   => searchByPartialDescription(); true
                case "sid"  => searchById(); true
                case "ud"   => updateDescription(); true
                case "udl"  => updateDeadline(); true
                case Exit() => exit(); false
                case _      => true
              }
            }

        loop(shouldKeepLooping = true)
      }

      private def descriptionPrompt: String =
        fancyConsole.getStrLnTrimmedWithPrompt("Please enter a description:")

      @scala.annotation.nowarn
      private def create(): Unit =
        descriptionPrompt.pipe { description =>
          withDeadlinePrompt { deadline =>
            boundary
              .createOne(Todo.Data(description, deadline))
              .tapAs(
                fancyConsole.putSuccess("Successfully created the new todo.")
              )
          }
        }

      private def deadlinePrompt: String =
        fancyConsole.getStrLnTrimmedWithPrompt(
          s"Please enter a deadline in the following format $DeadlinePromptFormat:"
        )

      private def withDeadlinePrompt(onSuccess: LocalDateTime => Unit): Unit =
        deadlinePrompt.pipe(toLocalDateTime).pipe {
          case Right(deadline) => onSuccess(deadline)
          case Left(error)     => fancyConsole.putError(error)
        }

      private def toLocalDateTime(
          input: String
        ): Either[String, LocalDateTime] = {
        val formatter =
          DateTimeFormatter
            .ofPattern(DeadlinePromptPattern)

        Try(LocalDateTime.parse(input, formatter))
          .toEither
          .left
          .map { _ =>
            val renderedInput: String =
              inColor(input)(scala.Console.YELLOW)

            s"\n$renderedInput does not match the required format $DeadlinePromptFormat.${scala.Console.RESET}"
          }
      }

      private def delete(): Unit =
        withIdPrompt { id =>
          withReadOne(id) { todo =>
            boundary
              .deleteOne(todo)
              .tapAs(fancyConsole.putSuccess("Successfully deleted the todo."))
          }
        }

      private def idPrompt: String =
        fancyConsole
          .getStrLnTrimmedWithPrompt("Please enter the id:")

      private def withIdPrompt(onValidId: String => Unit): Unit =
        idPrompt.pipe(toId).pipe {
          case Right(id)   => onValidId(id)
          case Left(error) => fancyConsole.putError(error)
        }

      private def toId(userInput: String): Either[String, String] =
        if (userInput.isEmpty || userInput.contains(" "))
          Left(
            s"\n${scala.Console.YELLOW + userInput + scala.Console.RED} is not a valid id.${scala.Console.RESET}"
          )
        else
          Right(userInput)

      private def deleteAll(): Unit =
        boundary
          .deleteAll
          .tapAs(fancyConsole.putSuccess("Successfully deleted all todos."))

      private def withReadOne(
          id: String
        )(
          onFound: Todo.Existing => Unit
        ): Unit =
        boundary
          .readOneById(id)
          .pipe {
            case Some(todo) => onFound(todo)
            case None       => displayNoTodosFoundMessage
          }

      private def showAll(): Unit =
        boundary.readAll.pipe(displayZeroOrMany)

      private def displayZeroOrMany(todos: Vector[Todo.Existing]): Unit =
        if (todos.isEmpty)
          displayNoTodosFoundMessage
        else {
          val uxMatters = if (todos.size == 1) "todo" else "todos"

          val renderedSize: String =
            inColor(todos.size.toString)(scala.Console.GREEN)

          fancyConsole
            .putStrLn(s"\nFound $renderedSize $uxMatters:\n")
            .tapAs {
              todos
                .sortBy(_.deadline)
                .map(renderedWithPattern)
                .foreach(println)
            }
        }

      private def displayNoTodosFoundMessage(): Unit =
        fancyConsole.putWarning("\nNo todos found!")

      private def renderedWithPattern(todo: Todo.Existing): String = {
        val renderedId: String =
          inColor(todo.id.toString)(scala.Console.GREEN)

        val renderedDescription: String =
          inColor(todo.description)(scala.Console.MAGENTA)

        val renderedDeadline: String =
          inColor(todo.deadline.format(pattern))(scala.Console.YELLOW)

        s"$renderedId $renderedDescription is due on $renderedDeadline."
      }

      private def searchByPartialDescription(): Unit =
        descriptionPrompt
          .pipe(boundary.readManyByPartialDescription)
          .pipe(displayZeroOrMany)

      private def searchById(): Unit =
        withIdPrompt { id =>
          boundary
            .readOneById(id)
            .pipe(_.to(Vector))
            .pipe(displayZeroOrMany)
        }

      @scala.annotation.nowarn
      private def updateDescription(): Unit =
        withIdPrompt { id =>
          withReadOne(id) { todo =>
            descriptionPrompt.pipe { description =>
              boundary
                .updateOne(todo.withUpdatedDescription(description))
                .tapAs(
                  fancyConsole
                    .putSuccess("Successfully updated the description.")
                )
            }
          }
        }

      @scala.annotation.nowarn
      private def updateDeadline(): Unit =
        withIdPrompt { id =>
          withReadOne(id) { todo =>
            withDeadlinePrompt { deadline =>
              boundary
                .updateOne(todo.withUpdatedDeadline(deadline))
                .tapAs(
                  fancyConsole.putSuccess("Successfully updated the deadline.")
                )
            }
          }
        }

      private def exit(): Unit =
        println("\nUntil next time!\n")
    }

  private val DeadlinePromptPattern: String =
    "yyyy-M-d H:m"

  private val DeadlinePromptFormat: String =
    inColor(DeadlinePromptPattern)(scala.Console.MAGENTA)

  private def inColor(line: String)(color: String): String =
    color + line + scala.Console.RESET
}
