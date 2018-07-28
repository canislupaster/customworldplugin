package com.thomas.customworld.commands.mod.ban

import com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class UnbanCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args) match {
      case (x:Player,_) if !x.hasPermission("tempban") => false
      case (_,Array(playername)) =>
        (new PlayerDB().autoClose(x => x.getUUIDFromName(playername)) match {
          case Some(x) =>
            if (new IpDB().autoClose(y => y.getIps(x) map (ip => y.removeBan(ip)) sum) > 0)
              SuccessMsg
            else ErrorMsg("nobans")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}