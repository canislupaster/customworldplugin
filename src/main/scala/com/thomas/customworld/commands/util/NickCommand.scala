package com.thomas.customworld.commands.util

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import com.thomas.customworld.player.nick
import org.bukkit.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class NickCommand() extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender,args) match {
      case (x:Player, y) if y.length > 0 =>
        if (new PlayerDB().autoClose(_.setNick(x.getUniqueId, spaceJoin(y.toList))) == 0) {
          ErrorMsg("alreadyexists") sendClient sender
        } else {
          nick.update(x,Some(spaceJoin(y.toList)))
          SuccessMsg sendClient sender
        }

        true
      case (x:Player, _) => false
      case _ => ErrorMsg("noconsole") sendClient sender; true
    }
  }
}
