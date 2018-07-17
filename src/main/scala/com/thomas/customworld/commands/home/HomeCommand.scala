package com.thomas.customworld.commands.home

import com.thomas.customworld.db.{DBConstructor, Home, HomeDB}
import com.thomas.customworld.messaging._
import org.bukkit.Location
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

class HomeCommand (sqldb: DBConstructor) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val db = new HomeDB (sqldb())

    (sender, args) match {
      case (x:Player, Array(y:String)) =>
        db.GetHomes(x.getUniqueId) filter {case Home(x,_,_,_,_) => x.toLowerCase.startsWith(y.toLowerCase)} match {
          case Array(home:Home) =>
            x.teleport(new Location(x.getServer.getWorld(home.World), home.X, home.Y, home.Z), TeleportCause.PLUGIN)
            HomeMessage(Some("tpto"), home) sendClient sender
          case homes =>
            InfoMsg ("homes",None) sendClient sender
            homes foreach (HomeMessage(None, _) sendClient sender)
        }

      case (x:Player, _) =>
        db.GetHomes(x.getUniqueId) match {
          case Array() =>
            ErrorMsg("nohomes") sendClient sender
          case homes =>
            InfoMsg ("homes",None) sendClient sender
            homes foreach (HomeMessage(None, _) sendClient sender)
        }

      case _ =>
        ErrorMsg ("noconsole") sendClient sender
    }

    true
  }
}