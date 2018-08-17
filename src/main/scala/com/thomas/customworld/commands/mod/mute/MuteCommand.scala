package scala.com.thomas.customworld.commands.mod.mute

import java.sql.Timestamp
import java.util.{Calendar, Date}

import scala.com.thomas.customworld.db._
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.ip.IpBan
import scala.com.thomas.customworld.player.mute.Mute
import scala.com.thomas.customworld.player.rank
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{OfflinePlayerArg, OnlinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.player.rank.UnMuted
import scala.com.thomas.customworld.utility._

class MuteCommand (mute:Boolean) extends PermissionCommand("mute", (sender, _, _, args) => {
    args.toList match {
      case OnlinePlayerArg(x)::TimeParser(time)::reason if mute && reason.nonEmpty =>
        val r = spaceJoin(reason)

        val ips = new IpDB().autoClose(_.getIps(x.getUniqueId))
        val cplayer = player.getPlayer(x)
        cplayer.updateRank(rank.Muted(cplayer.rank))
        PunishMsg (x, sender, "muted", r) globalBroadcast sender.getServer
        if (new MuteDB ().autoClose(x => ips map (y => x.addMute(Mute(y, Some(time), r))) sum) > 0) SomeArr(SuccessMsg) else SomeArr(ErrorMsg("alreadymuted"))

      case OfflinePlayerArg(x)::_ if !mute =>
        val ips = new IpDB().autoClose(_.getIps(x.playerid))
        sender.getServer.getPlayer(x username) match {case y:Player => player.getPlayer(y).updateRank(UnMuted(x.rank)); case _ => ()}

        if (new MuteDB().autoClose(x => ips map (y => x.removeMute(y)) sum) > 0) SomeArr(SuccessMsg) else SomeArr(ErrorMsg("alreadymuted"))

      case _ => None
    }
  })