package devinsideyou
package expressionproblem
package `final`

object Eval {
  val dsl: Exp[Int] =
    new Exp[Int] {
      override def lit(n: Int): Int = n
      override def neg(e: Int): Int = -e
      override def add(e1: Int, e2: Int): Int = e1 + e2
    }
}
