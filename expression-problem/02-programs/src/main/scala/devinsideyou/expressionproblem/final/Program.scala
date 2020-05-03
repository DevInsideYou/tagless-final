package devinsideyou
package expressionproblem
package `final`

trait Program[Repr] {
  def repr: Repr
}

object Program {
  def dsl[Repr](implicit exp: Exp[Repr]): Program[Repr] =
    new Program[Repr] {
      import exp._

      val repr: Repr =
        add(
          lit(16),
          neg(
            add(
              lit(1),
              lit(2)
            )
          )
        )
    }
}
