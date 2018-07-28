package com.thomas.customworld.commands.util

import com.thomas.customworld.messaging._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect

import scala.collection.JavaConversions._

class HealCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case player:Player =>
        player.setHealth(20.0)
        player.setFoodLevel(20)
        (player.getActivePotionEffects toSet) foreach ((x:PotionEffect) => player.removePotionEffect (x.getType ))
        SuccessMsg
      case _ =>
        ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}
