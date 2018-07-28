package com.thomas.customworld.commands.util

import com.thomas.customworld.messaging._
import org.bukkit.Location
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class WorldCommand (overworld: String, flatlands: String) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    sender match {
      case x:Player =>
        def gotoworld (y:String): Unit =
          x.teleport(sender.getServer.getWorld(y).getSpawnLocation)

        args match {
          case Array("overworld") =>
            gotoworld(overworld)
            InfoMsg (ConfigMsg("tpto"), RuntimeMsg("overworld")) sendClient sender; true
          case Array("flatlands") =>
            gotoworld(flatlands)
            InfoMsg (ConfigMsg("tpto"), RuntimeMsg("flatlands")) sendClient sender; true
          case _ => false
        }
      case _ => ErrorMsg("noconsole") sendClient sender; true
    }
  }
}
