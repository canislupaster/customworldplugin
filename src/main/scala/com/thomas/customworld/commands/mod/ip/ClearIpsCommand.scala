package scala.com.thomas.customworld.commands.mod.ip

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.utility._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OfflinePlayerArg, PermissionCommand}

class ClearIpsCommand extends PermissionCommand("manageips", (sender, cmd, label, args) => {
  args match {
    case Array(OfflinePlayerArg(x)) =>
      new IpDB().autoClose(y => y.removeIps(x.playerid))
      SomeArr(SuccessMsg)
    case _ => None
  }
})