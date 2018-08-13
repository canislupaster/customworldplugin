package scala.com.thomas.customworld.commands.home

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.db.{DBConstructor, HomeDB}
import scala.com.thomas.customworld.messaging._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.util.matching.Regex
import scala.com.thomas.customworld.utility._

import scala.com.thomas.customworld.commands.home.{Home, HomeCommand}

class SetHomeCommand extends HomeCommand((player, home, db) => {
  val num = db.GetNumHomes(player.getUniqueId)
  if (num > 30) {
    ErrorMsg("manyhomes")
  } else {
    val loc = player.getLocation
    db.SetHome (player.getUniqueId, Home(home, player.getWorld.getUID, loc.getBlockX, loc.getBlockY, loc.getBlockZ))
    SuccessMsg
  }
})