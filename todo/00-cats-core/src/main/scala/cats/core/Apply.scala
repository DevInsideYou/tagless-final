package cats

trait Apply[F[_]] extends Semigroupal[F] with Functor[F] {
  def map2[A, B, Result](fa: F[A], fb: F[B])(abr: (A, B) => Result): F[Result] =
    map(product(fa, fb))(abr.tupled)
}
