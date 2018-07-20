package com.thomas.customworld.commands

import com.thomas.customworld.messaging._
import com.thomas.customworld.minigame
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class HealCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case player:Player =>
        player.setHealth("20.0")
        player.removePotionEffect(player.getPotionEffects())
        player.sendMessage("You healed yourself.")
      case _ =>
        ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}
