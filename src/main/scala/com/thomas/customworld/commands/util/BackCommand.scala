package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class BackCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        getPlayer(x).beforeTp match {
          case Some(loc) =>
            x.teleport(loc)
            InfoMsg(ConfigMsg("tpto"), RuntimeMsg("last location"))
          case None => ErrorMsg("noloc")
        }
      case _ => ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}
