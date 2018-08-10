package scala.com.thomas.customworld.commands.mod.ban

import java.util.UUID

import scala.com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.ip.IpBan
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.util._

class BanCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args.toList) match {
      case (x:Player,_) if !x.hasPermission("ban") => false
      case (_,playername::reason) =>
        (new PlayerDB().autoClose(x => x.getPlayerFromName(playername)) match {
          case Some(x) =>
            val r = spaceJoin(reason)

            new IpDB().autoClose(y => y.getIps(x.playerid) foreach (ip => y.addBan(IpBan(ip, None, r)))) //TODO BAN IPS OF PLAYERS RELATED TO IP

            sender.getServer.getPlayer(playername) match {
              case player:Player => PunishMsg (player, sender, "banned", r) globalBroadcast sender.getServer; player.kickPlayer (r)
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