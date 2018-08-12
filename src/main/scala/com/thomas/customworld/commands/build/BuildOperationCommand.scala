package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.{CommandPart, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, Message, SuccessMsg}
import scala.com.thomas.customworld.util.{SomeArr, spaceJoin, toUUID}

class BuildOperationCommand(op: (Build, Player, BuildDB, List[String]) => Option[Array[Message]]) extends PermissionCommand("build",
  base.PlayerCommand((player, cmd, _, args) => {
    if (args.nonEmpty) {
      val db = new BuildDB()
      val argsl = args toList
      val msg:Option[Array[Message]] = db.getBuildByName(toUUID(player.getUniqueId), spaceJoin(argsl)) match {
        case Some (x) =>
          op(x, player, db, argsl)
        case None => SomeArr(ErrorMsg("nobuild"))
      }

      db close()
      msg
    } else None
  }))