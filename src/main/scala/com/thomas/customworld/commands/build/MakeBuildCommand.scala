package scala.com.thomas.customworld.commands.build

import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.base.{CommandPart, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.player.freeop.ProtectedRegion
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.util._

class MakeBuildCommand(cfg:FileConfiguration) extends PermissionCommand("build",
  base.PlayerCommand((player, cmd, _, args) => {
    (args.toList, getSelection(player)) match {
      case (x, _) if (spaceJoin(x) length) > base.NameLen => SomeArr(ErrorMsg("toolong"))
      case (theme::namearr, Some(x)) if (x.getWidth*x.getLength) < cfg.getInt("freeop.buildlimit") || player.hasPermission("spawnbuild") =>
        val name = spaceJoin(namearr)
        val themeid = new BuildDB().autoClose(_.getActiveThemeFromName(theme)) map (_.themeId)
        val newname:String = themeid match {case Some(_) => name; case _ => spaceJoin(theme::namearr)}
        if (newname.isEmpty) SomeArr(ErrorMsg("noname"))
        else {
          val region = new Box(x)
          freeop.protectedRegions find (_.region.intersectXZ(region)) match {
            case Some (_) => SomeArr(ErrorMsg("tooclose"))
            case None =>
              val uid = player.getUniqueId
              freeop.registerProtected(ProtectedRegion(region, List(uid)))
              if (new BuildDB().autoClose(db => db.addBuild(uid, newname, themeid, region)) > 0) SomeArr(SuccessMsg) else SomeArr(ErrorMsg("alreadyexists"))
          }
        }

      case (_::_, Some(x)) => SomeArr(ErrorMsg("sellarge"))
      case (_::_, None) => SomeArr(ErrorMsg("noselection"))
      case _ => None
    }
  }))
