package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{CommandPart, PlayerCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.util._

class DelBuildCommand extends PlayerCommand ("build") {
  override def commandPart: CommandPart = (sender, cmd, _, args) => {
    val player = sender.asInstanceOf[Player]

    if (args.nonEmpty) {
      new BuildDB().autoClose(_.getBuildByName(toUUID(player.getUniqueId), spaceJoin(args toList))) match {
        case Some (x) =>
          freeop.unRegisterProtected(x protectedRegion)
          new BuildDB().autoClose(_.removeBuild(x))
          SomeArr(SuccessMsg)
        case None => SomeArr(ErrorMsg("nobuild"))
      }
    } else None
  }
}
