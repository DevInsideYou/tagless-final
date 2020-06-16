package cats

trait Applicative[F[_]] extends Apply[F] {
  def pure[A](a: A): F[A]
}
