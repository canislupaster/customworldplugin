package scala.com.thomas.customworld.commands.build

import java.io.{File, IOException}

import com.sk89q.worldedit.extension.platform.MultiUserPlatform
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.internal.ServerInterfaceAdapter
import org.bukkit.{Location, World}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base._
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{PageMsg, _}
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.util._

class BuildCommand extends PermissionCommand("build", (sender, cmd, name, args) => {
  val db = new BuildDB()

  def msgs: Array[String] => Option[Array[Message]] = {
    case Array("list", PageArg(page)) =>
      val builds: Array[Message] = db.getBuilds(page).map(BuildMessage).toArray
      if (builds.isEmpty) SomeArr(ErrorMsg("nobuilds")) else Some(PageMsg(page) +: builds)

    case Array("list", OfflinePlayerArg(player), PageArg(page)) =>
      val builds: Array[Message] = db.getUserBuilds(player playerid, page).map(BuildMessage).toArray
      if (builds.isEmpty) SomeArr(ErrorMsg("nobuilds")) else Some(PageMsg(page) +: builds)

    case Array("list", x, PageArg(page)) =>
      db.getThemeFromName(x) match {
        case Some(theme) =>
          val builds: Array[Message] = db.getThemeBuilds(theme, page).map(BuildMessage).toArray
          if (builds.isEmpty) SomeArr(ErrorMsg("nobuilds")) else Some(PageMsg(page) +: builds)
        case None => SomeArr(ErrorMsg("nothemes"))
      }

    case Array("themes", PageArg(page)) =>
      val themes = db.getThemes(page).map(ThemeMessage).toArray
      if (themes.isEmpty) SomeArr(ErrorMsg("nothemes")) else Some(PageMsg(page) +: themes)

    case Array("list") => msgs(Array("list", "1"))
    case Array("themes") => msgs(Array("themes", "1"))
    case Array("list", x) => msgs(Array("list", x, "1"))
    case _ =>
      val player = Option(sender.asInstanceOf[Player])
      player flatMap (player => {
        args.toList match {
          case "tp" :: OfflinePlayerArg(buildplayer) :: buildname if buildname.nonEmpty =>
            new BuildDB().autoClose(_.getBuildByName(buildplayer.playerid, spaceJoin(buildname))) match {
              case Some(x) =>
                val loc = x.region.getCenter.add(0, 10, 0)
                player.teleport(new Location(x.region bworld, loc.getBlockX, loc.getBlockY, loc.getBlockZ))
                SomeArr(InfoMsg(ConfigMsg("tpto"), RuntimeMsg(spaceJoin(buildname))))
              case None => SomeArr(ErrorMsg("nobuild"))
            }
          case "in" :: _ =>
            val msg = new BuildDB().autoClose(_.inBuild(player.getLocation.getBlockX, player.getLocation.getBlockZ)) match {
              case Some(x) => InfoMsg(ConfigMsg("in"), BuildMessage(x))
              case None => ErrorMsg("nobuild")
            }

            SomeArr(msg)
          case _ => None
        }
      })
  }

  val msg = msgs(args)
  db.close()
  msg
})
