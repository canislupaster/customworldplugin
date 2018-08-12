package scala.com.thomas.customworld.discord

import java.io.{File, IOException}

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit
import com.thomas.customworld.DiscordBotJava
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import org.bukkit.scheduler.BukkitRunnable
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.{ActivityType, StatusType}

import scala.com.thomas.customworld.{CustomCore, EventModule}
import scala.com.thomas.customworld.commands.base.OfflinePlayerArg
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ConfigMsg, ErrorMsg, InfoMsg, SuccessMsg}
import scala.com.thomas.customworld.util.{SomeArr, spaceJoin}

object DiscordBot extends EventModule {
  var client:IDiscordClient = _

  override def enable(plugin:Plugin) {
    try {
      val cfg = plugin.getConfig
      client = new ClientBuilder().withToken(cfg.getString("discord.token")).login()
      client.changePresence(StatusType.IDLE, ActivityType.WATCHING, "you guys... creepily :)")
      client.getDispatcher.registerListener(new DiscordBotJava())
    } catch {
      case _ => ErrorMsg("reloadbot") globalBroadcast plugin.getServer
    }
  }

  def evDiscord (t: MessageReceivedEvent): Unit = {
    val channel = t.getMessage.getChannel

    t.getMessage.getContent split ' ' toList match {
      case "/schematic"::OfflinePlayerArg(buildplayer)::buildname if buildname.nonEmpty =>
        new BuildDB().autoClose(_.getBuildByName(buildplayer.playerid, spaceJoin(buildname))) match {
          case Some(x) =>
            InfoMsg(ConfigMsg("loading")) discordMessage channel
            val brun = new BukkitRunnable () {
              override def run(): Unit = {
                try {
                  val file = new File(s"plugins/CustomCore/build.schematic")
                  file.createNewFile() //delete if exists
                  val height = x.region.getWorld.getMaxY
                  val schematic = (x.region expand new Vector(0,height,0)) copyBox()
                  schematic.save(file, ClipboardFormat.SCHEMATIC)

                  SuccessMsg discordMessage channel
                  channel.sendFile(file)
                } catch {
                  case e: IOException => ConfigMsg("io") discordMessage channel
                }
              }
            }.runTask (CustomCore.plugin)
          case _ => ErrorMsg("nobuild") discordMessage channel
        }
      case "/schematic"::_::_ => ErrorMsg("invalidarg") discordMessage channel
      case _ => ()
    }
  }

  override def disable(plugin: Plugin): Unit = {
    client.logout()
  }
}
