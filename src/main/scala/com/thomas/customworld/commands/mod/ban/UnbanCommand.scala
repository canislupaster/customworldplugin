package scala.com.thomas.customworld.commands.mod.ban

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.util._

class UnbanCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args) match {
      case (x:Player,_) if !x.hasPermission("tempban") => false
      case (_,Array(playername)) =>
        (new PlayerDB().autoClose(x => x.getPlayerFromName(playername)) match {
          case Some(x) =>
            if (new IpDB().autoClose(y => y.getIps(x.playerid) map (ip => y.removeBan(ip)) sum) > 0)
              SuccessMsg
            else ErrorMsg("nobans")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}