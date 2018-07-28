package com.thomas.customworld.db

import java.sql.{Connection, ResultSet}
import java.util.UUID

import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.player.rank
import com.thomas.customworld.player.rank.Rank
import com.thomas.customworld.util._
import org.bukkit.entity.Player

class PlayerDB() extends MainDB() {

  def updateUser(playerid:String, playername:String): Unit = {
    data.update(sql"INSERT INTO player (playerid, username) VALUES ($playerid, $playername) ON DUPLICATE KEY UPDATE username=$playername")
  }

  def getUUIDFromName(playername:String): Option[String] = {
    data.selectFirst(sql"SELECT * FROM player WHERE LOWER(username) LIKE LOWER($playername)") { x =>
      x.getString("playerid")
    } match {
      case Some(x:String) => Some(x)
      case _ => None
    }
  }

  def getRank(playerid:String) : Rank = {
    data.selectFirst(sql"SELECT rankid FROM player WHERE playerid=$playerid") { x => x.getInt("rankid") }
      match {case Some(x:Int) => rank.ranks(x); case _ => rank.ranks.head}
  }

  def setRank(playerid:String, newrank:Rank) : Unit = {
    val ranknum = rank.ranks indexOf newrank
    data.update(sql"UPDATE player SET rankid=$ranknum WHERE playerid=$playerid")
  }

  def getNick(playerid:String) : Option[String] = {
    val x = data.selectFirst(sql"SELECT nickname FROM player WHERE playerid=$playerid") {x => x.getString("nickname")}
    x.flatMap (Option(_))
  }

  def setNick(playerid:String, nick:String) : Int = {
    data.update(sql"UPDATE IGNORE player SET nickname=$nick WHERE playerid=$playerid")
  }
}