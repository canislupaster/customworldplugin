package scala.com.thomas.customworld.commands

import scala.com.thomas.customworld.utility.UUID

package object home {
  case class Home(Name: String, World: UUID, X: Int, Y: Int, Z: Int)
}
