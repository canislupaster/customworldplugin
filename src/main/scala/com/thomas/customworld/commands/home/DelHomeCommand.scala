package com.thomas.customworld.commands.home

import com.thomas.customworld.db.{DBConstructor, HomeDB}
import com.thomas.customworld.messaging._
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class DelHomeCommand (sqldb: DBConstructor) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val db = new HomeDB(sqldb())

    ((sender, args) match {
      case (x: Player, y) =>
        db.DelHome(x.getUniqueId, y reduce ((a,b) => a+" "+b)) match {
          case false => ErrorMsg ("nohome")
          case true => SuccessMsg()
        }
      case _ => ErrorMsg ("noconsole")
    }).sendClient(sender)

    db close()
    true
  }
}