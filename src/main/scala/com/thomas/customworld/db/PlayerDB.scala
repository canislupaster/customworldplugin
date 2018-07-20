package com.thomas.customworld.db

import java.sql.{Connection, ResultSet}
import java.util.UUID

import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.rank
import com.thomas.customworld.util
import com.thomas.customworld.rank.Rank
import org.bukkit.entity.Player

class PlayerDB(conn: Connection) extends MainDB(conn) {

  def UpdateUser (playerid:UUID, playername:String): Unit = {
    val id = util.QuoteSurround(playerid.toString)
    val name = util.QuoteSurround(playername)
    data.update(sql"INSERT INTO player (playerid, username) VALUES (${playerid.toString}, $playername) ON DUPLICATE KEY UPDATE username=$playername")
  }

  def GetUUIDFromName (playername:String): Option[UUID] = {
    data.selectFirst(sql"SELECT * FROM player WHERE username=$playername") { x =>
      UUID.fromString(x.getString("playerid"))
    } match {
      case Some(x:UUID) => Some(x)
      case _ => None
    }
  }

  def GetRank (playerid:UUID) : Rank = {
    data.selectFirst(sql"SELECT rankid FROM player WHERE playerid=${playerid.toString}") { x => x.getInt("rankid") }
      match {case Some(x:Int) => rank.Ranks(x); case _ => rank.Ranks.head}
  }

  def SetRank (playerid:UUID, newrank:Rank) : Unit = {
    val ranknum = rank.Ranks indexOf newrank
    data.update(sql"UPDATE player SET rankid=$ranknum WHERE playerid=${playerid.toString}")
  }
}