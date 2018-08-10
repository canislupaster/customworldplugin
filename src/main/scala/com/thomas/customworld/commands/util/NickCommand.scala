package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}

import org.bukkit.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.player.nick.nickEventModule
import scala.com.thomas.customworld.util._

class NickCommand() extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender,args) match {
      case (x:Player, y) if y.length > 0 =>
        if (new PlayerDB().autoClose(db => db.updatePlayer(db.getPlayer(x.getUniqueId).copy(nickname=Some(spaceJoin(y.toList)))) == 0)) {
          ErrorMsg("alreadyexists") sendClient sender
        } else {
          nickEventModule.update(x,Some(spaceJoin(y.toList)))
          SuccessMsg sendClient sender
        }

        true
      case (x:Player, _) => false
      case _ => ErrorMsg("noconsole") sendClient sender; true
    }
  }
}
