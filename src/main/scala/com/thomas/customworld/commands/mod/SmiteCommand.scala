package com.thomas.customworld.commands.mod

import com.thomas.customworld.messaging._
import com.thomas.customworld.{player, util}
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}

class SmiteCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if (!sender.hasPermission("smite")) false
    else {
      args.toList match {
        case i :: x if sender.getServer.getPlayer(i) != null && x.nonEmpty =>
          val tplayer = sender.getServer.getPlayer(i)
          player.joinFreeOP(tplayer)

          tplayer.setGameMode(GameMode.SURVIVAL)
          tplayer.getWorld.strikeLightningEffect(tplayer.getLocation)
          tplayer.damage(500)
          tplayer.getInventory.clear()
          PunishMsg (tplayer, sender, "smited", util.spaceJoin(x)) globalBroadcast sender.getServer

          SuccessMsg sendClient sender
          true
        case _ :: _ => ErrorMsg("invalidarg") sendClient sender; true
        case _ => false
      }
    }
  }
}
