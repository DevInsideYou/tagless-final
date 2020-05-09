package devinsideyou
package expressionproblem

import scala.util.chaining._

import cats._
import cats.data._
import cats.instances.all._

object Main extends App {
  println("─" * 100)

  import `final`._

  Program
    .Expression
    .dsl[Id, Int](
      Evaluate.Literal.dsl,
      Evaluate.Negation.dsl,
      Evaluate.Addition.dsl
    )
    .run
    .tap(println)

  Program
    .Expression
    .dsl[Id, String](
      View.Literal.dsl,
      View.Negation.dsl,
      View.Addition.dsl
    )
    .run
    .tap(println)

  println("─" * 100)

  Program
    .Multiplication
    .dsl[Id, Int](
      Evaluate.Literal.dsl,
      Evaluate.Negation.dsl,
      Evaluate.Addition.dsl,
      Evaluate.Multiplication.dsl
    )
    .run
    .tap(println)

  Program
    .Multiplication
    .dsl[Id, String](
      View.Literal.dsl,
      View.Negation.dsl,
      View.Addition.dsl,
      View.Multiplication.dsl
    )
    .run
    .tap(println)

  println("─" * 100)

  Program
    .MultiplicationInTheMiddle
    .dsl[Id, Int](
      Evaluate.Literal.dsl,
      Evaluate.Negation.dsl,
      Evaluate.Addition.dsl,
      Evaluate.Multiplication.dsl
    )
    .run
    .tap(println)

  Program
    .MultiplicationInTheMiddle
    .dsl[Id, String](
      View.Literal.dsl,
      View.Negation.dsl,
      View.Addition.dsl,
      View.Multiplication.dsl
    )
    .run
    .tap(println)

  println("─" * 100)

  Program
    .Division
    .dsl[EitherNec[String, *], Int](
      Evaluate.Literal.dsl,
      Evaluate.Negation.dsl,
      Evaluate.Addition.dsl,
      Evaluate.Multiplication.dsl,
      Evaluate.Division.dsl
    )
    .run
    .tap(println)

  Program
    .Division
    .dsl[Id, String](
      View.Literal.dsl,
      View.Negation.dsl,
      View.Addition.dsl,
      View.Multiplication.dsl,
      View.Division.dsl
    )
    .run
    .tap(println)

  println("─" * 100)

  Program
    .DivisionInTheMiddle
    .dsl[EitherNec[String, *], Int](
      Evaluate.Literal.dsl,
      Evaluate.Negation.dsl,
      Evaluate.Addition.dsl,
      Evaluate.Multiplication.dsl,
      Evaluate.Division.dsl
    )
    .run
    .tap(println)

  Program
    .DivisionInTheMiddle
    .dsl[Id, String](
      View.Literal.dsl,
      View.Negation.dsl,
      View.Addition.dsl,
      View.Multiplication.dsl,
      View.Division.dsl
    )
    .run
    .tap(println)

  println("─" * 100)

  Program
    .DivisionWithTwoErrors
    .dsl[EitherNec[String, *], Int](
      Evaluate.Literal.dsl,
      Evaluate.Addition.dsl,
      Evaluate.Division.dsl
    )
    .run
    .tap(println)

  Program
    .DivisionWithTwoErrors
    .dsl[Id, String](
      View.Literal.dsl,
      View.Addition.dsl,
      View.Division.dsl
    )
    .run
    .tap(println)

  println("─" * 100)
}
