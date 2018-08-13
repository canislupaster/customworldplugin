package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.{CommandPart, OfflinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, Message, SuccessMsg}
import scala.com.thomas.customworld.utility._

class VoteCommand extends PermissionCommand("build", base.PlayerCommand((player, cmd, name, args) => {
    args match {
      case x => x.toList match {
        case Int(i) :: OfflinePlayerArg(voteplayer) :: buildname if buildname.nonEmpty && i>=0 && i<=5 =>
          val db = new BuildDB()
          val msg:Option[Array[Message]] = db.getBuildByName(voteplayer.playerid, spaceJoin(buildname)) match {
            case Some(build) if build.playerId != toUUID(player.getUniqueId) =>
              db.setVote(build, i, player.getUniqueId)
              SomeArr(SuccessMsg)
            case _ => SomeArr(ErrorMsg("nobuild"))
          }

          db.close()
          msg
        case _ => None
      }
    }
}))
