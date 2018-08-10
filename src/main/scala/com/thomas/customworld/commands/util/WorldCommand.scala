package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.messaging._
import org.bukkit.Location
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class WorldCommand (cfg:FileConfiguration) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    sender match {
      case x:Player =>
        def gotoworld (y:String): Unit =
          x.teleport(sender.getServer.getWorld(y).getSpawnLocation)

        args match {
          case Array("overworld") =>
            gotoworld(cfg.getString("world.overworld"))
            InfoMsg (ConfigMsg("tpto"), RuntimeMsg("overworld")) sendClient sender; true
          case Array("flatlands") =>
            gotoworld(cfg.getString("world.flatlands"))
            InfoMsg (ConfigMsg("tpto"), RuntimeMsg("flatlands")) sendClient sender; true
          case Array("competition") =>
            gotoworld(cfg.getString("world.competition"))
            InfoMsg (ConfigMsg("tpto"), RuntimeMsg("competition")) sendClient sender; true
          case _ => false
        }
      case _ => ErrorMsg("noconsole") sendClient sender; true
    }
  }
}
