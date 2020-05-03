package devinsideyou
package expressionproblem
package initial

object View {
  def interpret(exp: Exp): String =
    exp match {
      case Exp.Lit(n)      => s"${n}"
      case Exp.Neg(e)      => s"(-${interpret(e)})"
      case Exp.Add(e1, e2) => s"(${interpret(e1)} + ${interpret(e2)})"
    }
}
