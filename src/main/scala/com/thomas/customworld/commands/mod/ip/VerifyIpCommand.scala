package scala.com.thomas.customworld.commands.mod.ip

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.utility._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OnlinePlayerArg, PermissionCommand}

class VerifyIpCommand extends PermissionCommand("manageips", (sender,cmd, label, args) => {
    args match {
      case Array(OnlinePlayerArg(x)) =>
        new IpDB().autoClose (y => y.addIp(x.getUniqueId, x.getAddress.getAddress.getHostAddress) match {
          case 0 => SomeArr(ErrorMsg("alreadyexists"))
          case _ =>
            InfoMsg (RuntimeMsg(x.getName), ConfigMsg("verified"), RuntimeMsg(sender.getName)) globalBroadcast sender.getServer

            player.getPlayer(x) verify x
            SomeArr(SuccessMsg)
        })
      case _ => None
    }
  })