package com.thomas.customworld.commands

import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin

class SetTemplate (plugin: Plugin) extends CommandExecutor {
  var cfg: FileConfiguration = plugin.getConfig
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if (sender.hasPermission("config")) {
      val name = args match {
        case Array(x) => x
        case _ => "cage"
      }

      //TODO: IF NEEDED

    }

    true
  }
}
