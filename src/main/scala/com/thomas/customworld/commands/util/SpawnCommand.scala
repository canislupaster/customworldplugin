package com.thomas.customworld.commands.util

import com.thomas.customworld.messaging._
import com.thomas.customworld.{minigame, player}
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class SpawnCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        player.joinFreeOP(x)
        x.setGameMode(GameMode.CREATIVE)
        x.teleport(x.getWorld.getSpawnLocation)
        InfoMsg(ConfigMsg("tpto"), RuntimeMsg("spawn"))
      case _ =>
        ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}