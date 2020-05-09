package devinsideyou
package expressionproblem
package `final`

import cats._
import cats.syntax.all._

object View {
  object Literal {
    def dsl[F[_]: Applicative]: Literal[F, String] =
      new Literal[F, String] {
        override def literal(n: Int): F[String] =
          s"${n}".pure[F]
      }
  }

  object Negation {
    def dsl[F[_]: Functor]: Negation[F, String] =
      new Negation[F, String] {
        override def negate(a: F[String]): F[String] =
          a.map(a => s"(-${a})")
      }
  }

  object Addition {
    def dsl[F[_]: Apply: NonEmptyParallel]: Addition[F, String] =
      new Addition[F, String] {
        override def add(a1: F[String], a2: F[String]): F[String] =
          (a1, a2).parMapN((a1, a2) => s"(${a1} + ${a2})")
      }
  }

  object Multiplication {
    def dsl[F[_]: Apply: NonEmptyParallel]: Multiplication[F, String] =
      new Multiplication[F, String] {
        override def multiply(a1: F[String], a2: F[String]): F[String] =
          (a1, a2).parMapN((a1, a2) => s"(${a1} * ${a2})")
      }
  }

  object Division {
    def dsl[F[_]: Apply: NonEmptyParallel]: Division[F, String] =
      new Division[F, String] {
        override def divide(a1: F[String], a2: F[String]): F[String] =
          (a1, a2).parMapN((a1, a2) => s"(${a1} / ${a2})")
      }
  }
}
