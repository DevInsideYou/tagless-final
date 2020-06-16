package cats

trait Monad[F[_]] extends Applicative[F] with FlatMap[F] {
  final def iterateWhile[A](fa: F[A])(p: A => Boolean): F[A] =
    flatMap(fa) { a =>
      if (p(a))
        iterateWhile(fa)(p)
      else
        pure(a)
    }
}
