package scala.com.thomas.customworld.commands.mod.ban

import java.util.UUID

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.ip.IpBan
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OfflinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.utility._

class BanCommand extends PermissionCommand("ban", (sender, cmd, label, args) => {
    args.toList match {
      case OfflinePlayerArg(x)::reason =>
        val r = spaceJoin(reason)

        new IpDB().autoClose(y => y.getIps(x.playerid) foreach (ip => y.addBan(IpBan(ip, None, r)))) //TODO BAN IPS OF PLAYERS RELATED TO IP

        sender.getServer.getPlayer(x.playerid UUID) match {
          case player:Player => PunishMsg (player, sender, "banned", r) globalBroadcast sender.getServer; player.kickPlayer (r)
          case _ => ()
        }

        SomeArr(SuccessMsg)
      case _ => None
    }
  })