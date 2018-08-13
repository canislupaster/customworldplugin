package scala.com.thomas.customworld.player

import java.sql.Timestamp

import scala.com.thomas.customworld.CustomCore._
import scala.com.thomas.customworld.db.{IpDB, MuteDB}
import scala.com.thomas.customworld.messaging.{ConfigMsg, ErrorMsg, InfoMsg, RuntimeMsg}
import scala.com.thomas.customworld.utility._
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.{Cancellable, Event}
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerEvent, PlayerLoginEvent}

import scala.com.thomas.customworld.EventModule

object ipEventModule extends EventModule {
  override def join (player: Player): Unit = {
    val cplayer = getPlayer(player)
    val ip = player.getAddress.getAddress.getHostAddress
    val v =
      new IpDB().autoClose(x => {
        val ips = x.getIps(player.getUniqueId)
        if (ips.isEmpty) {
          x.addIp (player.getUniqueId, ip)
          true
        } else ips contains ip
      })

    if (v) cplayer verify player else {
      InfoMsg(RuntimeMsg(player.getName), ConfigMsg("imposter")) globalBroadcast player.getServer
      ErrorMsg("unverified") sendClient player
    }
  }

  override def ev (ev: Event): Unit = ev match {
    case ev:PlayerLoginEvent =>
      val ip = ev.getAddress.getHostAddress
      new IpDB().autoClose(x => x.getBans(ip)) match {
        case x::_ =>
          val reason =
            s"""
               |${ChatColor.RED}You have been${if (x.time.isDefined) " temporarily" else ""} banned!
               |${cfg.getString("lang.appeal")}
               |${ChatColor.YELLOW}${x.time match {case Some(time) => s"You will be unbanned at ${time.toString}"
                  case _ => "This is a permanent ban."}}
             """.stripMargin.replaceAll("\\r\\n", "\n")
          ev.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason)
        case _ => ()
    }
    case _ => ()
  }
}