package devinsideyou
package expressionproblem
package `final`

trait Literal[F[_], A] {
  def literal(n: Int): F[A]
}

trait Negation[F[_], A] {
  def negate(a: F[A]): F[A]
}

trait Addition[F[_], A] {
  def add(a1: F[A], a2: F[A]): F[A]
}

trait Multiplication[F[_], A] {
  def multiply(a1: F[A], a2: F[A]): F[A]
}

trait Division[F[_], A] {
  def divide(a1: F[A], a2: F[A]): F[A]
}
