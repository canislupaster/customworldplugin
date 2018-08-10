package scala.com.thomas.customworld.commands.home

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.PlayerCommand
import scala.com.thomas.customworld.db.{DBConstructor, HomeDB}
import scala.com.thomas.customworld.messaging._
import org.bukkit.Location
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import scala.com.thomas.customworld.util._

import scala.com.thomas.customworld.commands.home.{Home, HomeCommand}

class HomeTpCommand extends PlayerCommand("home") {
  override def commandPart: CommandPart = (sender, cmd, name, args) => {
    val player = sender.asInstanceOf[Player]
    def teleportto (home:Home) = {
      val loc = new Location (sender.getServer.getWorld(home.World UUID), home.X, home.Y, home.Z)
      player.teleport(loc)
    }

    val msg:Array[Message] = new HomeDB().autoClose(db => {
      val homes = db.GetHomes(player.getUniqueId) filter { case Home(str, _, _, _, _) => str.toLowerCase.startsWith(spaceJoin(args.toList).toLowerCase) }
      homes.toList match {
        case x::List() => teleportto(x); Array(InfoMsg(ConfigMsg("tpto"),RuntimeMsg(x.Name)))
        case x::_ => homes map (x => HomeMessage(x))
        case List() => Array(ErrorMsg("nohomes"))
      }
    })

    Some(msg)
  }
}