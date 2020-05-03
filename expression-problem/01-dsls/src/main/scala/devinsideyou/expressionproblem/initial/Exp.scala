package devinsideyou
package expressionproblem
package initial

sealed abstract class Exp extends Product with Serializable
object Exp {
  final case class Lit(n: Int) extends Exp
  final case class Neg(e: Exp) extends Exp
  final case class Add(e1: Exp, e2: Exp) extends Exp
}
