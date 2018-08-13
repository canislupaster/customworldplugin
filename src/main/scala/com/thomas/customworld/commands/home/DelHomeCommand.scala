package scala.com.thomas.customworld.commands.home

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.db.{DBConstructor, HomeDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.utility._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.home.HomeCommand

class DelHomeCommand extends HomeCommand((player, name, db) =>
  if (db.DelHome(player.getUniqueId, name)) SuccessMsg else ErrorMsg("nohome")
)