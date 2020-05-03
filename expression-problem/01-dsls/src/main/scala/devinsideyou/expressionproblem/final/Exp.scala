package devinsideyou
package expressionproblem
package `final`

trait Exp[Repr] {
  def lit(n: Int): Repr
  def neg(e: Repr): Repr
  def add(e1: Repr, e2: Repr): Repr
}
