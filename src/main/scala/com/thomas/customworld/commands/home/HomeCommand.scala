package com.thomas.customworld.commands.home

import com.thomas.customworld.db.{DBConstructor, HomeDB}
import com.thomas.customworld.messaging._
import org.bukkit.Location
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import com.thomas.customworld.util._

case class Home(Name: String, World: String, X: Int, Y: Int, Z: Int)
class HomeCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val db = new HomeDB()

    sender match {
      case x:Player =>
        db.GetHomes(x.getUniqueId) filter {case Home(str,_,_,_,_) => str.toLowerCase.startsWith(spaceJoin(args.toList).toLowerCase)} match {
          case Array(home:Home) =>
            x.teleport(new Location(x.getServer.getWorld(toUUID(home.World)), home.X, home.Y, home.Z), TeleportCause.PLUGIN)
            HomeMessage(Some("tpto"), home) sendClient sender
          case Array() =>
            ErrorMsg("nohomes") sendClient sender
          case homes =>
            InfoMsg (ConfigMsg("homes")) sendClient sender
            homes foreach (HomeMessage(None, _) sendClient sender)
        }

      case _ =>
        ErrorMsg ("noconsole") sendClient sender
    }

    true
  }
}