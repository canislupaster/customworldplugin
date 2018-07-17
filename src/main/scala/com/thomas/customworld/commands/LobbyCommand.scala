package com.thomas.customworld.commands

import com.thomas.customworld.messaging._
import com.thomas.customworld.minigame
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class LobbyCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        minigame.leave(x)
        x.teleport(x.getWorld.getSpawnLocation)
        InfoMsg("tpto", Some("lobby"))
      case _ =>
        ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}