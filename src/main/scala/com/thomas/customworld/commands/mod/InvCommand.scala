package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.{OnlinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.messaging.SuccessMsg
import scala.com.thomas.customworld.utility

class InvCommand extends PermissionCommand("inv", base.PlayerCommand((sender, _, _, args) => {
  args match {
    case Array(OnlinePlayerArg(x)) =>
      sender.openInventory(x.getOpenInventory)
      utility.SomeArr(SuccessMsg)
    case _ => None
  }
}))
