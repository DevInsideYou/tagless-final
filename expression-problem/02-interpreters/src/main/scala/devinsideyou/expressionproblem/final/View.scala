package devinsideyou
package expressionproblem
package `final`

object View {
  val dsl: Expression[String] =
    new Expression[String] {
      override def literal(n: Int): String =
        s"${n}"

      override def negate(a: String): String =
        s"(-${a})"

      override def add(a1: String, a2: String): String =
        s"(${a1} + ${a2})"
    }
}
