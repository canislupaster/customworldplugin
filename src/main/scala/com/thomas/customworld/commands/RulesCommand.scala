package com.thomas.customworld.commands

import com.thomas.customworld.messaging.{ConfigMsg, InfoMsg}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}

class RulesCommand () extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    InfoMsg(ConfigMsg("rules")) sendClient sender
    true
  }
}
