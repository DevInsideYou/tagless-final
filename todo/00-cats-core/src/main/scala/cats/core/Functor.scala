package cats

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(ab: A => B): F[B]
}
