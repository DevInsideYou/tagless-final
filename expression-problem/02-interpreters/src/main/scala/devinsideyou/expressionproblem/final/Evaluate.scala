package devinsideyou
package expressionproblem
package `final`

import cats._
import cats.data._
import cats.syntax.all._

object Evaluate {
  object Literal {
    def dsl[F[_]: Applicative]: Literal[F, Int] =
      new Literal[F, Int] {
        override def literal(n: Int): F[Int] =
          n.pure[F]
      }
  }

  object Negation {
    def dsl[F[_]: Functor]: Negation[F, Int] =
      new Negation[F, Int] {
        override def negate(a: F[Int]): F[Int] =
          a.map(-_)
      }
  }

  object Addition {
    def dsl[F[_]: Apply: NonEmptyParallel]: Addition[F, Int] =
      new Addition[F, Int] {
        override def add(a1: F[Int], a2: F[Int]): F[Int] =
          (a1, a2).parMapN(_ + _)
      }
  }

  object Multiplication {
    def dsl[F[_]: Apply: NonEmptyParallel]: Multiplication[F, Int] =
      new Multiplication[F, Int] {
        override def multiply(a1: F[Int], a2: F[Int]): F[Int] =
          (a1, a2).parMapN(_ * _)
      }
  }

  object Division {
    def dsl[
        F[_]: MonadError[*[_], NonEmptyChain[String]]: NonEmptyParallel
      ]: Division[F, Int] =
      new Division[F, Int] {
        override def divide(a1: F[Int], a2: F[Int]): F[Int] =
          (a1, a2).parTupled.flatMap {
            case (a1, 0) =>
              "division by zero"
                .pure[NonEmptyChain]
                .raiseError[F, Int]

            case (a1, a2) =>
              if (a1 % a2 == 0)
                (a1 / a2)
                  .pure[F]
              else
                "division ended up having rest"
                  .pure[NonEmptyChain]
                  .raiseError[F, Int]
          }
      }
  }
}
