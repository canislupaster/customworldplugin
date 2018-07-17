package com.thomas.customworld.commands.home

import com.thomas.customworld.db.{DBConstructor, Home, HomeDB}
import com.thomas.customworld.messaging._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.util.matching.Regex

class SetHomeCommand (sqldb: DBConstructor) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val db = new HomeDB(sqldb())

    ((sender, args) match {
      case (x: Player, y) if db.GetNumHomes(x.getUniqueId) < 30 =>
        val validate = new Regex("\\w")
        val str = y reduce ((a,b) => a+" "+b)
        validate findFirstIn str match {
          case Some(_) =>
            db.SetHome(x.getUniqueId, Home(str, x.getWorld.getUID, x.getLocation.getBlockX, x.getLocation.getBlockY, x.getLocation.getBlockZ))
            SuccessMsg()
          case None =>
            ErrorMsg("invalidarg")
        }
      case (x: Player, _) =>
        ErrorMsg ("manyhomes")
      case _ => ErrorMsg ("noconsole")
    }).sendClient(sender)

    db close()
    true
  }
}