package scala.com.thomas.customworld.commands.mod.ban

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OfflinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.utility._

class UnbanCommand extends PermissionCommand("tempban", (sender, cmd, label, args) => {
    args match {
      case Array(OfflinePlayerArg(x)) =>
        if (new IpDB().autoClose(y => y.getIps(x.playerid) map (ip => y.removeBan(ip)) sum) > 0) SomeArr(SuccessMsg)
        else SomeArr(ErrorMsg("nobans"))
      case _ => None
    }
  })