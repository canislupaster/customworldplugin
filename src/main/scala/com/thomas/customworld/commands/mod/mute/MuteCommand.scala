package com.thomas.customworld.commands.mod.mute

import java.sql.Timestamp
import java.util.{Calendar, Date}

import com.thomas.customworld.db._
import com.thomas.customworld.messaging._
import com.thomas.customworld.player
import com.thomas.customworld.player.ip.IpBan
import com.thomas.customworld.player.mute.Mute
import com.thomas.customworld.player.rank
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class MuteCommand (mute:Boolean) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("mute") => false
      case (_, playername::TimeParser(time)::reason) if mute && reason.nonEmpty =>
        (sender.getServer.getPlayer(playername) match {
          case x:Player =>
            val r = spaceJoin(reason)

            val ips = new IpDB().autoClose(_.getIps(x.getUniqueId))
            player.getPlayer(x).updateRank(rank.Muted)
            PunishMsg (x, sender, "muted", r) globalBroadcast sender.getServer
            if (new MuteDB ().autoClose(x => ips map (y => x.addMute(Mute(y, Some(time), r))) sum) > 0) SuccessMsg else ErrorMsg("alreadymuted")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case (_, playername::_) if !mute =>
        (new PlayerDB().autoClose(_.getUUIDFromName(playername)) match {
          case Some (x) =>
            val ips = new IpDB().autoClose(_.getIps(x))
            val rank = new PlayerDB().autoClose(_.getRank(x))
            sender.getServer.getPlayer(playername) match {case x:Player => player.getPlayer(x).updateRank(rank); case _ => ()}

            if (new MuteDB().autoClose(x => ips map (y => x.removeMute(y)) sum) > 0) SuccessMsg else ErrorMsg("alreadymuted")
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}