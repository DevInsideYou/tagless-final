package devinsideyou
package expressionproblem
package `final`

trait Program[A] {
  def run: Option[A]
}

object Program {
  object Expression {
    def dsl[A](implicit expression: Expression[A]): Program[A] =
      new Program[A] {
        import expression._

        override val run: Option[A] =
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
    def dsl[A](
        implicit
        expression: Expression[A],
        multiplication: Multiplication[A]
      ): Program[A] =
      new Program[A] {
        import expression._
        import multiplication._

        override val run: Option[A] =
          multiply(
            literal(2),
            Expression.dsl.run
          )
      }
  }

  object MultiplicationInTheMiddle {
    def dsl[A](
        implicit
        expression: Expression[A],
        multiplication: Multiplication[A]
      ): Program[A] =
      new Program[A] {
        import expression._
        import multiplication._

        override val run: Option[A] =
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
    def dsl[A](
        implicit
        expression: Expression[A],
        multiplication: Multiplication[A],
        division: Division[A]
      ): Program[A] =
      new Program[A] {
        import expression._
        import multiplication._
        import division._

        override val run: Option[A] =
          divide(
            Multiplication.dsl.run,
            literal(2)
          )
      }
  }

  object DivisionInTheMiddle {
    def dsl[A](
        implicit
        expression: Expression[A],
        multiplication: Multiplication[A],
        division: Division[A]
      ): Program[A] =
      new Program[A] {
        import expression._
        import multiplication._
        import division._

        override val run: Option[A] =
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
}
