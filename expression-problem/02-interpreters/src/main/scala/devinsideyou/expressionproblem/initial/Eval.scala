package devinsideyou
package expressionproblem
package initial

object Eval {
  def interpret(exp: Exp): Int =
    exp match {
      case Exp.Lit(n)      => n
      case Exp.Neg(e)      => -interpret(e)
      case Exp.Add(e1, e2) => interpret(e1) + interpret(e2)
    }
}
