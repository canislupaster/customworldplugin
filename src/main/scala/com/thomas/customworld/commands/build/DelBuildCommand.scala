package scala.com.thomas.customworld.commands.build

import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.util._

class DelBuildCommand extends BuildOperationCommand((build, player, db, args) => {
    db.removeBuild(build)
    freeop.unRegisterProtected(build protectedRegion)
    SomeArr(SuccessMsg)
  })
