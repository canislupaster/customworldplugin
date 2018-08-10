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

import scala.com.thomas.customworld.player.rank.UnMuted
import scala.com.thomas.customworld.util._

class MuteCommand (mute:Boolean) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("mute") => false
      case (_, playername::TimeParser(time)::reason) if mute && reason.nonEmpty =>
        (sender.getServer.getPlayer(playername) match {
          case x:Player =>
            val r = spaceJoin(reason)

            val ips = new IpDB().autoClose(_.getIps(x.getUniqueId))
            val cplayer = player.getPlayer(x)
            cplayer.updateRank(rank.Muted(cplayer.rank))
            PunishMsg (x, sender, "muted", r) globalBroadcast sender.getServer
            if (new MuteDB ().autoClose(x => ips map (y => x.addMute(Mute(y, Some(time), r))) sum) > 0) SuccessMsg else ErrorMsg("alreadymuted")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case (_, playername::_) if !mute =>
        (new PlayerDB().autoClose(_.getPlayerFromName(playername)) match {
          case Some (x) =>
            val ips = new IpDB().autoClose(_.getIps(x.playerid))
            sender.getServer.getPlayer(playername) match {case y:Player => player.getPlayer(y).updateRank(UnMuted(x.rank)); case _ => ()}

            if (new MuteDB().autoClose(x => ips map (y => x.removeMute(y)) sum) > 0) SuccessMsg else ErrorMsg("alreadymuted")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}