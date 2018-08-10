package scala.com.thomas.customworld.db

import java.sql.{Connection, Date, Timestamp}

import scala.com.thomas.customworld.util._
import com.github.takezoe.scala.jdbc._
import scala.com.thomas.customworld.player.ip.IpBan


class IpDB() extends MainDB() {
  def addIp: (UUID, String) => Int = { case (UUID(playerid), ip) =>
    data.update (sql"INSERT IGNORE INTO ip VALUES ($playerid, INET_ATON($ip))")
  }

  def getIps: UUID => Seq[String] = { case UUID(playerid) =>
    data.select (sql"SELECT INET_NTOA(ip) FROM ip WHERE playerid=$playerid") { x =>
      x.getString("INET_NTOA(ip)")
    }
  }

  def removeIps: UUID => Int = { case UUID(playerid) =>
    data.update (sql"DELETE FROM ip WHERE playerid=$playerid")
  }

  def getBans (ip:String): Seq[IpBan] = {
    data.select(sql"SELECT * FROM ipban WHERE ip=INET_ATON($ip) AND (time IS NULL OR time > NOW())") {x => IpBan(ip, Option(x.getTimestamp("time")), x.getString("reason"))}
  }

  def removeBan (ip:String): Int = {
    data.update(sql"DELETE FROM ipban WHERE ip=INET_ATON($ip)")
  }

  def addBan (ipBan: IpBan): Int = {
    data.update(sql"INSERT INTO ipban VALUES (INET_ATON(${ipBan.ip}), ${ipBan.reason}, ${ipBan.time})")
  }
}