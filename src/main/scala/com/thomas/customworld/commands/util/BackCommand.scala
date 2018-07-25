package com.thomas.customworld.commands

import com.thomas.customworld.messaging._
import com.thomas.customworld.player._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class BackCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender match {
      case x:Player =>
        getPlayer(x).beforeTp match {
          case Some(loc) =>
            x.teleport(loc)
            InfoMsg("tpto", Some("last location"))
          case None => ErrorMsg("noloc")
        }
      case _ => ErrorMsg("noconsole")
    }) sendClient sender

    true
  }
}
