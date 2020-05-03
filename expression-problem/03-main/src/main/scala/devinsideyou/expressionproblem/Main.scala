package devinsideyou
package expressionproblem

object Main extends App {
  println("─" * 100)

  import `final`._

  println(Program.dsl(Evaluate.dsl).run)
  println(Program.dsl(View.dsl).run)

  println("─" * 100)
}
