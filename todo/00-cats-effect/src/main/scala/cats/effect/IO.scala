package cats
package effect

trait IO[A] {
  def unsafeRunSync(): A
}

object IO {
  implicit val dsl: Sync[IO] =
    new Sync[IO] {
      override def pure[A](a: A): IO[A] = { () =>
        a
      }

      override def defer[A](fa: => IO[A]): IO[A] = { () =>
        fa.unsafeRunSync()
      }

      override def delay[A](a: => A): IO[A] = { () =>
        a
      }

      override def map[A, B](fa: IO[A])(ab: A => B): IO[B] = { () =>
        val a = fa.unsafeRunSync()
        val b = ab(a)

        b
      }

      override def product[A, B](fa: IO[A], fb: IO[B]): IO[(A, B)] = { () =>
        val a = fa.unsafeRunSync()
        val b = fb.unsafeRunSync()

        a -> b
      }

      override def flatMap[A, B](fa: IO[A])(afb: A => IO[B]): IO[B] = { () =>
        val a = fa.unsafeRunSync()
        val fb = afb(a)

        val b = fb.unsafeRunSync()

        b
      }
    }
}
