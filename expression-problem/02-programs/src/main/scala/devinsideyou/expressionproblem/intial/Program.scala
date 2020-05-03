package devinsideyou
package expressionproblem
package initial

object Program {
  import Exp._

  val exp: Exp =
    Add(
      Lit(16),
      Neg(
        Add(
          Lit(1),
          Lit(2)
        )
      )
    )
}
