package scala.com.thomas.customworld.commands.build

import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.{CommandPart, PlayerCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.player.freeop.ProtectedRegion
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.util._

class MakeBuildCommand(cfg:FileConfiguration) extends PlayerCommand("build") { //TODO: moduralize configuz
  override def commandPart: CommandPart = (sender, cmd, _, args) => {
    val player = sender.asInstanceOf[Player]

    (args.toList, getSelection(player)) match {
      case (theme::namearr, Some(x)) if (x.getWidth*x.getLength) < cfg.getInt("freeop.buildlimit") || player.hasPermission("spawnbuild") =>
        val name = spaceJoin(namearr)
        val themeid = new BuildDB().autoClose(_.getActiveThemeFromName(theme)) map (_.themeId)
        val newname:String = themeid match {case Some(_) => name; case _ => theme+" "+name}
        if (newname.isEmpty) SomeArr(ErrorMsg("noname"))
        else {
          val region = new Box(player.getWorld, x)
          freeop.protectedRegions find (_.region.intersectXZ(region)) match {
            case Some (_) => SomeArr(ErrorMsg("tooclose"))
            case None =>
              val uid = player.getUniqueId
              freeop.registerProtected(ProtectedRegion(region, List(uid)))
              if (new BuildDB().autoClose(db => db.addBuild(uid, newname, themeid, region)) > 0) {
                new BuildDB().autoClose(db => db.getBuildByName(uid, newname) foreach (b => db.setVote(b, uid)))
                SomeArr(SuccessMsg)
              }  else SomeArr(ErrorMsg("alreadyexists"))
          }
        }

      case (_::_, Some(x)) => SomeArr(ErrorMsg("sellarge"))
      case (_::_, None) => SomeArr(ErrorMsg("noselection"))
      case _ => None
    }
  }
}
