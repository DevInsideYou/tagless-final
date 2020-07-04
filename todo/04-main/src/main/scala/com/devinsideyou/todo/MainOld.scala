package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

object MainOld /* extends App */ {
  val crudController: crud.ControllerOld =
    crud
      .DependencyGraphOld
      .dsl(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm"))

  crudController.run()
}
