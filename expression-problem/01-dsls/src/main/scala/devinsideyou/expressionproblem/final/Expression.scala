package devinsideyou
package expressionproblem
package `final`

trait Expression[A] {
  def literal(n: Int): Option[A]
  def negate(a: Option[A]): Option[A]
  def add(a1: Option[A], a2: Option[A]): Option[A]
}

trait Multiplication[A] {
  def multiply(a1: Option[A], a2: Option[A]): Option[A]
}

trait Division[A] {
  def divide(a1: Option[A], a2: Option[A]): Option[A]
}
