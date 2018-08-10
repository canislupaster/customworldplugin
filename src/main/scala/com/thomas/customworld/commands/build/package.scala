package scala.com.thomas.customworld.commands

import java.sql.Timestamp

import scala.com.thomas.customworld.player.freeop.ProtectedRegion
import scala.com.thomas.customworld.util.{Box, UUID}

package object build {
  case class Theme(themeId:Long, name:String, timeStarting:Timestamp, timeEnding:Timestamp)
  case class Build(buildId:Long, playerId:UUID, region:Box, buildName:String, theme: Option[Theme], timeCreated:Timestamp) {
    def protectedRegion = ProtectedRegion(region, List(playerId))
  }
}
