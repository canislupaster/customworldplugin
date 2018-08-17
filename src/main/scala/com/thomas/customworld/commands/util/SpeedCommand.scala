package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.utility._
import scala.com.thomas.customworld.commands.base._
import scala.com.thomas.customworld.messaging.SuccessMsg

class SpeedCommand extends PermissionCommand ("speed", PlayerCommand((player, cmd, str, args) => {
  args match {
    case Array("fly", Dbl(n)) => player.setFlySpeed(n.floatValue()*0.1f); SomeArr(SuccessMsg)
    case Array("walk", Dbl(n)) => player.setWalkSpeed(n.floatValue()*0.2f); SomeArr(SuccessMsg)
    case _ => None
  }
}))
