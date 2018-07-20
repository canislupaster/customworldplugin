package com.thomas.customworld.commands

import com.thomas.customworld.messaging._
import com.thomas.customworld.minigame
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class SpawnCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        minigame.leave(x)
        x.setGameMode(GameMode.CREATIVE)
        x.teleport(x.getWorld.getSpawnLocation)
        InfoMsg("tpto", Some("spawn"))
      case _ =>
        ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}