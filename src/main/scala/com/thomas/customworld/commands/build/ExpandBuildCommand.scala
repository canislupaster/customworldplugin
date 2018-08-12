package scala.com.thomas.customworld.commands.build

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{CommandPart, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, Message, SuccessMsg}
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.player.freeop.ProtectedRegion
import scala.com.thomas.customworld.util._

class ExpandBuildCommand(cfg:FileConfiguration) extends BuildOperationCommand((build, player, db, args) => {
  getSelection(player) match {
    case Some (x) if (x.getWidth*x.getLength) < cfg.getInt("freeop.buildlimit") || player.hasPermission("spawnbuild") =>
      val b = new Box (x)

      val msg:Message =
        if (freeop.protectedRegions exists (p => p.region.intersectXZ(b) && p != build.protectedRegion)) ErrorMsg("tooclose")
        else {
          val newb = build copy (region = b setY 0)
          db.updateBuild (newb)
          freeop.unRegisterProtected(build protectedRegion)
          freeop.registerProtected(newb protectedRegion)
          SuccessMsg
        }

      SomeArr(msg)
    case Some (_) => SomeArr(ErrorMsg("sellarge"))
    case None => SomeArr(ErrorMsg("noselection"))
  }
})
