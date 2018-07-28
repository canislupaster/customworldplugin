package com.thomas.customworld.player

import java.sql.Timestamp

import com.thomas.customworld.CustomWorldPlugin._
import com.thomas.customworld.db.{IpDB, MuteDB}
import com.thomas.customworld.messaging.{ConfigMsg, ErrorMsg, InfoMsg, RuntimeMsg}
import com.thomas.customworld.util._
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.player.{PlayerEvent, PlayerLoginEvent}

package object ip {
  case class IpBan (ip: String, time: Option[Timestamp], reason:String)

  def join (player: Player): Unit = {
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

    if(v) {cplayer.verify()} else InfoMsg(RuntimeMsg(player.getName), ConfigMsg("imposter"))
  }

  def ev[Event <: PlayerEvent with Cancellable] (event: Event): Unit = {
    if (!getPlayer(event.getPlayer).verified) {
      ErrorMsg("unverified") sendClient event.getPlayer
      event.setCancelled(true)
    }
  }

  def login (ev: PlayerLoginEvent): Unit = {
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
  }
}