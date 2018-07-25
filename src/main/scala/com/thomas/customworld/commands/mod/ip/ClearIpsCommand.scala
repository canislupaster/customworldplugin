package com.thomas.customworld.commands.ip

import com.thomas.customworld.db.{DBConstructor, IpDB, PlayerDB}
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
          case None => ErrorMsg("invalidarg")
          case Some(x:String) => new IpDB().autoClose (y => y.removeIps(x)); SuccessMsg
        }) sendClient sender

        true
      case _ => false
    }
  }
}