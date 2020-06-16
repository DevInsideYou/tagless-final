package cats

trait FlatMap[F[_]] {
  def flatMap[A, B](fa: F[A])(ab: A => F[B]): F[B]
}
