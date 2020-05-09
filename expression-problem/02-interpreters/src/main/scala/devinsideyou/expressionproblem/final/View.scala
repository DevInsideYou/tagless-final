package devinsideyou
package expressionproblem
package `final`

object View {
  object Expression {
    val dsl: Expression[String] =
      new Expression[String] {
        override def literal(n: Int): Option[String] =
          Some(s"${n}")

        override def negate(a: Option[String]): Option[String] =
          a.map(a => s"(-${a})")

        override def add(
            a1: Option[String],
            a2: Option[String]
          ): Option[String] =
          a1.zip(a2).map {
            case (a1, a2) => s"(${a1} + ${a2})"
          }
      }
  }

  object Multiplication {
    val dsl: Multiplication[String] =
      new Multiplication[String] {
        override def multiply(
            a1: Option[String],
            a2: Option[String]
          ): Option[String] =
          a1.zip(a2).map {
            case (a1, a2) => s"(${a1} * ${a2})"
          }
      }
  }

  object Division {
    val dsl: Division[String] =
      new Division[String] {
        override def divide(
            a1: Option[String],
            a2: Option[String]
          ): Option[String] =
          a1.zip(a2).map {
            case (a1, a2) => s"(${a1} / ${a2})"
          }
      }
  }
}
