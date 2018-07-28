package com.thomas.customworld.commands.mod.ban

import java.sql.Timestamp
import java.util.{Calendar, Date}

import com.thomas.customworld.db._
import com.thomas.customworld.messaging.{ErrorMsg, PunishMsg, SuccessMsg}
import com.thomas.customworld.player
import com.thomas.customworld.player.ip.IpBan
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class TempBanCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("tempban") => false
      case (_, playername::TimeParser(time)::reason) =>
        (new PlayerDB().autoClose(x => x.getUUIDFromName(playername)) match {
          case Some(x:String) =>
            val r = spaceJoin(reason)

            new IpDB().autoClose(y => y.getIps(x) foreach (ip => y.addBan(IpBan(ip, Some(time), r))))

            sender.getServer.getPlayer(playername) match {
              case player:Player => PunishMsg (player, sender, "tempbanned", r) globalBroadcast sender.getServer; player.kickPlayer (r)
              case _ => ()
            }

            SuccessMsg
          case _ => ErrorMsg("noplayer")
        }) sendClient sender

        true
      case _ => false
    }
  }
}
