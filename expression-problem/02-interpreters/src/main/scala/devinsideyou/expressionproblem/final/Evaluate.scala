package devinsideyou
package expressionproblem
package `final`

object Evaluate {
  val dsl: Expression[Int] =
    new Expression[Int] {
      override def literal(n: Int): Int =
        n

      override def negate(a: Int): Int =
        -a

      override def add(a1: Int, a2: Int): Int =
        a1 + a2
    }
}
