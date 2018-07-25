package com.thomas.customworld.commands.mod

import com.thomas.customworld.db.DBConstructor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}

class CoordCommand (sqldb: DBConstructor) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    true //TODO MINIGAME CONFIGURER
  }
}
