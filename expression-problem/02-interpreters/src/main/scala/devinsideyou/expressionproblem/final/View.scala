package devinsideyou
package expressionproblem
package `final`

object View {
  val dsl: Exp[String] =
    new Exp[String] {
      override def lit(n: Int): String = s"${n}"
      override def neg(e: String): String = s"(-${e})"
      override def add(e1: String, e2: String): String = s"(${e1} + ${e2})"
    }
}
