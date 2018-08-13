package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.db.{DBConstructor, MuteDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, PunishMsg, SuccessMsg}
import scala.com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OnlinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.utility._

class KickCommand extends PermissionCommand("kick", (sender, cmd, label, args) => {
    args.toList match {
      case OnlinePlayerArg(x)::reason =>
        val r = spaceJoin(reason)

        PunishMsg (x, sender, "kicked", r) globalBroadcast sender.getServer
        x.kickPlayer(r)

        SomeArr(SuccessMsg)
      case _ => None
    }
  })