package scala.com.thomas.customworld.commands

import java.sql.Timestamp

import org.bukkit.entity.Player

import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.freeop.ProtectedRegion
import scala.com.thomas.customworld.utility.{Box, UUID, spaceJoin, toUUID}

package object build {
  case class Theme(themeId:Long, name:String, timeStarting:Timestamp, timeEnding:Timestamp)
  case class Build(buildId:Long, playerId:UUID, region:Box, buildName:String, theme: Option[Theme], timeCreated:Timestamp) {
    def protectedRegion = ProtectedRegion(region, List(playerId))
  }

  object BuildArg {
    def unapply(arg:String, p:Player): Option[Build] =
      new BuildDB().autoClose(_.getBuildByName(toUUID(p.getUniqueId), arg))
  }
}
