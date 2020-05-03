package devinsideyou
package expressionproblem
package `final`

trait Expression[A] {
  def literal(n: Int): A
  def negate(a: A): A
  def add(a1: A, a2: A): A
}
