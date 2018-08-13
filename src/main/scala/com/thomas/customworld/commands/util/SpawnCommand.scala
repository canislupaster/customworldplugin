package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.{minigame, player}
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.utility.SomeArr

class SpawnCommand(cfg:FileConfiguration) extends PermissionCommand("spawn",
  base.PlayerCommand((x, cmd, name, args) => {
    player.joinFreeOP(x)
    x.setGameMode(GameMode.CREATIVE)
    SomeArr(InfoMsg(ConfigMsg("tpto"), RuntimeMsg("spawn")))
  }))