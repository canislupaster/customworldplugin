package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.messaging._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect

import scala.collection.JavaConverters._
import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.util.SomeArr

class HealCommand extends PermissionCommand("heal", base.PlayerCommand((sender, command, label, args) => {
    val player:Player = sender.asInstanceOf[Player]
    player.setHealth(20.0)
    player.setFoodLevel(20)
    (player.getActivePotionEffects.asScala toSet) foreach ((x:PotionEffect) => player.removePotionEffect (x.getType ))
    SomeArr(SuccessMsg)
  }))