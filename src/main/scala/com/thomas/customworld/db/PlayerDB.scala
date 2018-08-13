package scala.com.thomas.customworld.db

import java.sql.{Connection, ResultSet}

import com.github.takezoe.scala.jdbc._
import scala.com.thomas.customworld.player.{CustomPlayer, rank}
import scala.com.thomas.customworld.player.rank.Rank
import scala.com.thomas.customworld.utility
import org.bukkit.entity.Player
import scala.com.thomas.customworld.utility.UUID

class PlayerDB() extends MainDB() {
  def assembleplayer (sql:SqlTemplate): Option[CustomPlayer] = {data.selectFirst(sql) { x =>
    val prank = x.getInt("rankid") match {case x:Int => rank.ranks(x); case _ => rank.ranks.head}
    CustomPlayer(UUID(x.getString("playerid")), x.getString("username"), prank, Option(x.getString("nickname")))
  }}

  def getPlayerFromName (playername:String): Option[CustomPlayer] = {
    assembleplayer(sql"SELECT * FROM player WHERE LOWER(username) LIKE LOWER($playername)")
  }

  def getPlayer: UUID => CustomPlayer = { case UUID(playerid) =>
    assembleplayer(sql"SELECT * FROM player WHERE playerid=$playerid").get
  }

  def updatePlayer: CustomPlayer => Int = { case CustomPlayer(UUID(id), name, prank, nick) =>
    val ranknum = rank.ranks indexOf prank
    data.update(sql"UPDATE player SET rankid=$ranknum, nickname=$nick, username=$name WHERE playerid=$id")
  }

  def upsertPlayer: (UUID, String) => Unit = { case (UUID(id), username) =>
    data.update(sql"INSERT INTO player (playerid, username) VALUES ($id, $username) ON DUPLICATE KEY UPDATE username=$username")
  }
}