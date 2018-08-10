package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{CommandPart, OfflinePlayerArg, PlayerCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, Message, SuccessMsg}
import scala.com.thomas.customworld.util._

class VoteCommand extends PlayerCommand("build") {
  override def commandPart: CommandPart = (sender, cmd, name, args) => {
    val player = sender.asInstanceOf[Player]

    args match {
      case x => x.toList match {
        case OfflinePlayerArg(voteplayer) :: buildname if buildname.nonEmpty =>
          val db = new BuildDB()
          val msg:Option[Array[Message]] = db.getBuildByName(voteplayer.playerid, spaceJoin(buildname)) match {
            case Some(build) => if (db.setVote(build, player.getUniqueId) > 0) SomeArr(SuccessMsg) else SomeArr(ErrorMsg("alreadyvoted"))
            case None => SomeArr(ErrorMsg("nobuild"))
          }

          db.close()
          msg
        case _ => None
      }
    }
  }
}
