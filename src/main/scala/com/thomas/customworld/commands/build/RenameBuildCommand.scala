package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.{CommandPart, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.utility.{SomeArr, spaceJoin, toUUID}

class RenameBuildCommand extends PermissionCommand("build", base.PlayerCommand((sender, cmd, _, args) => {
  val player = sender.asInstanceOf[Player]
  spaceJoin(args toList).split(" -> ") match {
    case Array(before, after) if before.nonEmpty && after.nonEmpty =>
      BuildArg.unapply(before, player) match {
        case Some(x) =>
          new BuildDB().autoClose(_.updateBuild(x copy (buildName = after)))
          SomeArr(SuccessMsg)
        case None => SomeArr(ErrorMsg("nobuild"))
      }

    case _ => None
  }
}))
