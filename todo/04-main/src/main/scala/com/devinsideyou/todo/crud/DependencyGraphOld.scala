package com.devinsideyou
package todo
package crud

import java.time.format.DateTimeFormatter

object DependencyGraphOld {
  def dsl(
      pattern: DateTimeFormatter
    )(implicit
      fancyConsole: FancyConsoleOld,
      random: RandomOld
    ): ControllerOld =
    ControllerOld.dsl(
      pattern = pattern,
      boundary = BoundaryOld.dsl(
        gateway = InMemoryEntityGatewayOld.dsl
      )
    )
}
