package scala.com.thomas.customworld.commands.util

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import org.bukkit.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base._
import scala.com.thomas.customworld.player.nick.nickEventModule
import scala.com.thomas.customworld.utility._

class NickCommand() extends PermissionCommand("nick", PlayerCommand ((player, cmd, _, args) => {
  val nick = spaceJoin(args toList) match {case x if x.length==0 => None; case x => Some(x);}
  if (nick exists (_.length > base.NameLen)) SomeArr(ErrorMsg("toolong")) else {
    if (new PlayerDB().autoClose(db => {
      db.updatePlayer(db.getPlayer(toUUID(player.getUniqueId)) copy(nickname=nick))
    }) > 0) {
      nickEventModule.update(player, nick)
      SomeArr(SuccessMsg)
    } else SomeArr(ErrorMsg("alreadyexists"))
  }
}))
