package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

object DependencyGraph {
  def dsl(
      pattern: DateTimeFormatter
    )(implicit
      fancyConsole: FancyConsole,
      random: Random
    ): Controller =
    Controller.dsl(
      pattern = pattern,
      boundary = BoundaryOld.dsl(
        gateway = InMemoryEntityGateway.dsl
      )
    )
}
