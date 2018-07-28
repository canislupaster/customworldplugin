package com.thomas.customworld.player

import java.sql.Timestamp

import com.thomas.customworld.CustomWorldPlugin._
import com.thomas.customworld.db.{IpDB, MuteDB, PlayerDB}
import com.thomas.customworld.player
import org.bukkit.entity.Player
import com.thomas.customworld.util._

package object mute {
  case class Mute (ip: String, time: Option[Timestamp], reason:String)

  def join (player:Player): Unit = {
    val mutes = new IpDB().autoClose(x => x.getIps(player.getUniqueId) flatMap (x => new MuteDB().autoClose(_.getMutes(x))))
    if (mutes.nonEmpty) getPlayer(player).updateRank(rank.Muted)
  }
}
