package scala.com.thomas.customworld.commands.tpa

import org.bukkit.command.{Command, CommandExecutor, CommandSender}

class TpRequest extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    true
  }
}
