package scala.com.thomas.customworld.commands.util

import org.bukkit.GameMode

import scala.com.thomas.customworld.commands.base._
import scala.com.thomas.customworld.messaging.SuccessMsg
import scala.com.thomas.customworld.utility._

class GamemodeCommand(gamemode:GameMode) extends PermissionCommand("gamemode", PlayerCommand((sender, cmd, str, args) => {
  args match {
    case Array() => sender.setGameMode(gamemode); SomeArr(SuccessMsg)
    case Array(OnlinePlayerArg(x)) if sender.hasPermission("setothergamemode") =>
      x.setGameMode(gamemode); SomeArr(SuccessMsg)
    case _ => None
  }
}))
