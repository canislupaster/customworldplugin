package com.thomas.customworld.commands.mod

import com.thomas.customworld.db.{DBConstructor, MuteDB}
import com.thomas.customworld.messaging.{ErrorMsg, PunishMsg, SuccessMsg}
import com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class KickCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("ban") => false
      case (_,playername::reason) =>
        (sender.getServer.getPlayer(playername) match {
          case x:Player =>
            val r = spaceJoin(reason)

            PunishMsg (x, sender, "kicked", r) globalBroadcast sender.getServer
            x.kickPlayer(r)

            SuccessMsg
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}