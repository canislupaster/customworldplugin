package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.messaging.SuccessMsg
import scala.com.thomas.customworld.utility

class ClearCommand extends PermissionCommand("clear", base.PlayerCommand((sender, _, _, args) => {
  sender.getInventory.clear()
  utility.SomeArr(SuccessMsg)
}))
