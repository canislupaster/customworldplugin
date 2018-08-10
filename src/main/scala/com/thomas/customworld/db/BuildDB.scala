package scala.com.thomas.customworld.db

import java.awt.print.Pageable
import java.sql.{ResultSet, Timestamp}

import com.github.takezoe.scala.jdbc._
import org.bukkit.util.BlockVector

import scala.com.thomas.customworld.CustomCore
import scala.com.thomas.customworld.commands.build.{Build, Theme}
import scala.com.thomas.customworld.util.{Box, Paginator, UUID}

class BuildDB extends MainDB {
  def assembletheme (x:ResultSet): Theme = {
    Theme(x.getLong("themeid"), x.getString("themename"),
      x.getTimestamp("timestarting"), x.getTimestamp("timeending"))
  }

  def getThemeFromId (themeid:Long): Option[Theme] = {
    data.selectFirst(sql"SELECT * FROM theme WHERE themeid=$themeid")(assembletheme)
  }

  def assemblebuild (x:ResultSet): Build = {
    val server = CustomCore.server
    val box = new Box(server.getWorld(UUID(x.getString("worldid")).UUID), new BlockVector(x.getInt("minx"), 0, x.getInt("minz")), new BlockVector(x.getInt("maxx"), 0, x.getInt("maxz")))

    Build(x.getLong("buildId"), UUID(x.getString("playerid")), box, x.getString("buildname"), getThemeFromId(x.getLong("themeid")),
      x.getTimestamp("timecreated"))
  }

  def getThemeBuilds (theme: Theme, page:Paginator): Seq[Build] = {
    data.select(sql"SELECT build.* FROM build LEFT JOIN buildvote ON build.buildid = buildvote.buildid WHERE build.themeid=${theme.themeId} GROUP BY build.buildid ORDER BY COUNT(build.buildid) DESC LIMIT ${page.from},${page.to}") (assemblebuild)
  }

  def getUserBuilds: (UUID, Paginator) => Seq[Build] = { case (UUID(id), page) =>
    data.select (sql"SELECT build.* FROM build LEFT JOIN buildvote ON build.buildid = buildvote.buildid WHERE build.playerid=$id GROUP BY build.buildid ORDER BY COUNT(build.buildid) DESC LIMIT ${page.from},${page.to}")(assemblebuild)
  }

  def getBuilds (page:Paginator): Seq[Build] = {
    data.select(sql"SELECT build.* FROM build LEFT JOIN buildvote ON build.buildid = buildvote.buildid GROUP BY build.buildid ORDER BY COUNT(build.buildid) DESC LIMIT ${page.from},${page.to}")(assemblebuild)
  }

  def getAllBuilds: Seq[Build] = {
    data.select(sql"SELECT * FROM build")(assemblebuild)
  }

  def getThemes (page:Paginator): Seq[Theme] = {
    data.select(sql"SELECT * FROM theme ORDER BY timeending DESC LIMIT ${page.from},${page.to} ") (assembletheme)
  }

  def addTheme (name:String, timeStarting:Timestamp, timeEnding:Timestamp) : Int = {
    data.update(sql"INSERT INTO theme (themename, timestarting, timeending) VALUES ($name, $timeStarting, $timeEnding)")
  }

  def getThemeFromName (name:String): Option[Theme] = {
    data.selectFirst(sql"SELECT * FROM theme WHERE LOWER(themename) LIKE LOWER($name)")(assembletheme)
  }

  def getActiveThemeFromName (name:String): Option[Theme] = {
    data.selectFirst(sql"SELECT * FROM theme WHERE timeending > NOW() AND timestarting < NOW() AND LOWER(themename) LIKE LOWER($name)")(assembletheme)
  }

  def addBuild: (UUID, String, Option[Long], Box) => Int = {
    case (UUID(playerid), name, themeid, Box(world, min, max)) => data.update(
      sql"INSERT INTO build (themeid, playerid, buildname, timecreated, worldid, minx, minz, maxx, maxz) VALUES ($themeid, $playerid, $name, NOW(), ${UUID.unapply(world.getUID)}, ${min.getBlockX}, ${min.getBlockZ}, ${max.getBlockX}, ${max.getBlockZ})")
  }

  def removeBuild(build: Build): Int = {
    removeVotes(build)
    data.update(sql"DELETE FROM build WHERE buildid=${build.buildId}")
  }

  def getBuildByName: (UUID, String) => Option[Build] = {
    case (UUID(playerid), name) => data.selectFirst(sql"SELECT * FROM build WHERE playerid=$playerid AND LOWER(buildname) LIKE LOWER($name)") (assemblebuild)
  }

  type buildVote = UUID
  def assemblevote (x:ResultSet) :buildVote = UUID(x.getString("playerid"))

  def removeVotes (build:Build) = {
    data.update(s"DELETE FROM buildvote WHERE buildid=${build.buildId}")
  }

  def getVotes (build:Build): Seq[buildVote] = {
    data.select(sql"SELECT playerid FROM buildvote WHERE buildid=${build.buildId}")(assemblevote)
  }

  def getVote: (Build, UUID) => Option[buildVote] = { case (build, UUID(id)) =>
    data.selectFirst(sql"SELECT playerid FROM buildvote WHERE buildid=${build.buildId} AND playerid=$id")(assemblevote)
  }

  def setVote: (Build, UUID) => Int = { case (build, UUID(id)) =>
    data.update(sql"INSERT IGNORE INTO buildvote VALUES ($id, ${build.buildId})")
  }

  def inBuild (x:Int, z:Int): Option[Build] = {
    data.selectFirst(sql"SELECT * FROM build WHERE $x > minx AND $z > minz AND $x < maxx AND $z < maxz")(assemblebuild)
  }
}
