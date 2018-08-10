package scala.com.thomas.customworld.commands.home

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.{PermissionCommand, PlayerCommand}
import scala.com.thomas.customworld.db.{DBConstructor, HomeDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.util._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class HomeCommand(operation: (Player, String, HomeDB) => Message) extends PlayerCommand("home") {
  override def commandPart:CommandPart = (sender, _, _, args) => {
    val db = new HomeDB()
    val player = sender.asInstanceOf[Player]

    val homename = if (args.nonEmpty) spaceJoin(args.toList) else "home"

    val msg = operation(player, homename, db)
    db close()

    SomeArr(msg)
  }
}