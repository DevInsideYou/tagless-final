package devinsideyou
package expressionproblem
package `final`

trait Program[A] {
  def run: A
}

object Program {
  def dsl[A](implicit expression: Expression[A]): Program[A] =
    new Program[A] {
      import expression._

      override val run: A =
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
