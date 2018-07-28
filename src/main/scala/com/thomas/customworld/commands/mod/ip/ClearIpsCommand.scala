package com.thomas.customworld.commands.mod.ip

import com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import com.thomas.customworld.util._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class ClearIpsCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args) match {
      case (x:Player,_) if !x.hasPermission("manageips") => false
      case (_, Array(x)) =>
        (new PlayerDB().autoClose(_.getUUIDFromName(x)) match {
          case Some(x:String) => new IpDB().autoClose (y => y.removeIps(x)); SuccessMsg
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}