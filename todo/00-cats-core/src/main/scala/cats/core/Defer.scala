package cats

trait Defer[F[_]] {
  def defer[A](fa: => F[A]): F[A]
}
