package devinsideyou
package expressionproblem
package `final`

trait Program[F[_], A] {
  def run: F[A]
}

object Program {
  object Expression {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        N: Negation[F, A],
        A: Addition[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, N._, A._

        override val run: F[A] =
          add(
            literal(16),
            negate(
              add(
                literal(1),
                literal(2)
              )
            )
          )
      }
  }

  object Multiplication {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        N: Negation[F, A],
        A: Addition[F, A],
        M: Multiplication[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, M._

        override val run: F[A] =
          multiply(
            literal(2),
            Expression.dsl.run
          )
      }
  }

  object MultiplicationInTheMiddle {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        N: Negation[F, A],
        A: Addition[F, A],
        M: Multiplication[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, N._, A._, M._

        override val run: F[A] =
          add(
            literal(16),
            negate(
              multiply(
                literal(2),
                add(
                  literal(1),
                  literal(2)
                )
              )
            )
          )
      }
  }

  object Division {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        N: Negation[F, A],
        A: Addition[F, A],
        M: Multiplication[F, A],
        D: Division[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, D._

        override val run: F[A] =
          divide(
            Multiplication.dsl.run,
            literal(2)
          )
      }
  }

  object DivisionInTheMiddle {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        N: Negation[F, A],
        A: Addition[F, A],
        M: Multiplication[F, A],
        D: Division[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, N._, A._, M._, D._

        override val run: F[A] =
          add(
            literal(16),
            negate(
              divide(
                multiply(
                  literal(2),
                  add(
                    literal(1),
                    literal(2)
                  )
                ),
                literal(2)
              )
            )
          )
      }
  }

  object DivisionWithTwoErrors {
    def dsl[F[_], A](
        implicit
        L: Literal[F, A],
        A: Addition[F, A],
        D: Division[F, A]
      ): Program[F, A] =
      new Program[F, A] {
        import L._, A._, D._

        override val run: F[A] =
          add(
            divide(
              literal(3),
              literal(0)
            ),
            divide(
              literal(3),
              literal(5)
            )
          )
      }
  }
}
