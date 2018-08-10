package scala.com.thomas.customworld.commands.mod.ban

import java.sql.Timestamp
import java.util.{Calendar, Date}

import scala.com.thomas.customworld.db._
import scala.com.thomas.customworld.messaging.{ErrorMsg, PunishMsg, SuccessMsg}
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.ip.IpBan
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.util._

class TempBanCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("tempban") => false
      case (_, playername::TimeParser(time)::reason) =>
        (new PlayerDB().autoClose(x => x.getPlayerFromName(playername)) match {
          case Some(x) =>
            val r = spaceJoin(reason)

            new IpDB().autoClose(y => y.getIps(x.playerid) foreach (ip => y.addBan(IpBan(ip, Some(time), r))))

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
