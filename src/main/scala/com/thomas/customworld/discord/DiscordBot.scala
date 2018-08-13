package scala.com.thomas.customworld.discord

import java.io.{File, IOException}

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit
import com.thomas.customworld.DiscordBotJava
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent
import org.bukkit.event.Event
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import org.bukkit.scheduler.BukkitRunnable
import github.scarsz.discordsrv.api.events

import scala.com.thomas.customworld.{CustomCore, EventModule}
import scala.com.thomas.customworld.commands.base.OfflinePlayerArg
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.utility.{SomeArr, spaceJoin}

object DiscordBot extends EventModule {
  val sub = new DiscordBotJava()

  override def enable(plugin:Plugin) {
    try {
      DiscordSRV.api.subscribe(sub)
    } catch {
      case _ => ErrorMsg("reloadbot") globalBroadcast plugin.getServer
    }
  }

  def evDiscord (t: DiscordGuildMessageReceivedEvent): Unit = {
    val channel = t.getMessage.getChannel

    if (t.getMessage.getContentStripped.startsWith("/")) {
      t.getMessage.getContentStripped.stripPrefix("/") split ' ' toList match {
        case "schematic" :: OfflinePlayerArg(buildplayer) :: buildname if buildname.nonEmpty =>
          new BuildDB().autoClose(_.getBuildByName(buildplayer.playerid, spaceJoin(buildname))) match {
            case Some(x) =>
              InfoMsg(ConfigMsg("loading")) discordMessage channel
              val brun = new BukkitRunnable() {
                override def run(): Unit = {
                  try {
                    val file = new File(s"plugins/CustomCore/build.schematic")
                    file.createNewFile() //delete if exists
                    val height = x.region.getWorld.getMaxY
                    val schematic = (x.region expand new Vector(0, height, 0)) copyBox()
                    schematic.save(file, ClipboardFormat.SCHEMATIC)

                    SuccessMsg discordMessage channel
                    channel.sendFile(file).queue()
                  } catch {
                    case e: IOException => ConfigMsg("io") discordMessage channel
                  }
                }
              }.runTask(CustomCore.plugin)
            case _ => ErrorMsg("nobuild") discordMessage channel
          }
        case "schematic" :: _ :: _ => ErrorMsg("invalidarg") discordMessage channel
        case cmd :: args if CustomCore.plugin.getCommand(cmd) != null =>
          if (CustomCore.plugin.getCommand(cmd).execute(new scala.com.thomas.customworld.discord.DiscordCommandSender(channel), cmd, args.toArray))
            () else InfoMsg(ErrorMsg("usage"), RuntimeMsg(CustomCore.plugin.getCommand(cmd).getUsage)) discordMessage channel
        case _ => ErrorMsg("nocmd") discordMessage channel
      }
    }
  }

  override def disable(plugin: Plugin): Unit = {
    DiscordSRV.api.unsubscribe(sub)
  }
}
