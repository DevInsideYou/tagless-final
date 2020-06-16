package cats

trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
}
