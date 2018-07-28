package com.thomas.customworld.commands.mod.ip

import com.thomas.customworld.db.{DBConstructor, IpDB, MuteDB}
import com.thomas.customworld.messaging._
import com.thomas.customworld.player
import com.thomas.customworld.util._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class VerifyIpCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args) match {
      case (x:Player, _) if !x.hasPermission("manageips") => false
      case (_, Array(i:String)) =>
        sender.getServer.getPlayer(i) match {
          case x:Player =>
            new IpDB().autoClose (y => y.addIp(x.getUniqueId, x.getAddress.getAddress.getHostAddress) match {
              case 0 => ErrorMsg("alreadyexists")
              case _ =>
                InfoMsg (RuntimeMsg(x.getName), ConfigMsg("verified"), RuntimeMsg(sender.getName)) globalBroadcast sender.getServer

                player.getPlayer(x).verify()
                SuccessMsg
            }) sendClient sender
          case _ => ErrorMsg("noplayer") sendClient sender
        }

        true
      case _ => false
    }
  }
}