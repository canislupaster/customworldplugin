package scala.com.thomas.customworld.commands.tpa

import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class TpAccept extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        player.getPlayer(x)
        SuccessMsg
      case _ => ErrorMsg ("noconsole")
    }) sendClient sender

    true
  }
}
