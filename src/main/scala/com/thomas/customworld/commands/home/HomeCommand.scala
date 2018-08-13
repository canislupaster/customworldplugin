package scala.com.thomas.customworld.commands.home

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.db.{DBConstructor, HomeDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.utility._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base

class HomeCommand(operation: (Player, String, HomeDB) => Message) extends PermissionCommand("home",
  base.PlayerCommand((player, _, _, args) => {
    val db = new HomeDB()

    val homename = if (args.nonEmpty) spaceJoin(args.toList) else "home"

    val msg = operation(player, homename, db)
    db close()

    SomeArr(msg)
  }))