package scala.com.thomas.customworld.player.mute

import java.sql.Timestamp

import scala.com.thomas.customworld.CustomCore._
import scala.com.thomas.customworld.db.{IpDB, MuteDB, PlayerDB}
import scala.com.thomas.customworld.player
import org.bukkit.entity.Player
import scala.com.thomas.customworld.player._

import scala.com.thomas.customworld.utility._
import scala.com.thomas.customworld.EventModule
import scala.com.thomas.customworld.player.rank

object muteEventModule extends EventModule {
  override def join (player:Player): Unit = {
    val mutes = new IpDB().autoClose(x => x.getIps(player.getUniqueId) flatMap (x => new MuteDB().autoClose(_.getMutes(x))))
    if (mutes.nonEmpty) {
      val cplayer = getPlayer(player)
      cplayer.updateRank(rank.Muted(cplayer.rank))
    }
  }
}
