package cats

trait FlatMap[F[_]] {
  def flatMap[A, B](fa: F[A])(afb: A => F[B]): F[B]
}
