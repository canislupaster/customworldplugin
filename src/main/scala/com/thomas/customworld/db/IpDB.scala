package com.thomas.customworld.db

import java.sql.{Connection, Date, Timestamp}
import java.util.UUID

import com.thomas.customworld.util._
import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.player.ip.IpBan


class IpDB() extends MainDB() {
  def addIp(playerid: String, ip: String): Int = {
    data.update (sql"INSERT IGNORE INTO ip VALUES ($playerid, INET_ATON($ip))")
  }

  def getIps(playerid: String): Seq[String] = {
    data.select (sql"SELECT INET_NTOA(ip) FROM ip WHERE playerid=$playerid") { x =>
      x.getString("INET_NTOA(ip)")
    }
  }

  def removeIps (playerid: String): Int = {
    data.update (sql"DELETE FROM ip WHERE playerid=$playerid")
  }

  def getBans (ip:String): Seq[IpBan] = {
    data.select(sql"SELECT * FROM ipban WHERE ip=INET_ATON($ip) AND (time IS NULL OR time > NOW())") {x => IpBan(ip, Option(x.getTimestamp("time")), x.getString("reason"))}
  }

  def removeBan (ip:String): Int = {
    data.update(sql"DELETE FROM ipban WHERE ip=INET_ATON($ip)")
  }

  def addBan (ipBan: IpBan): Int = {
    ipBan.time match {
      case Some(x) => data.update(sql"INSERT INTO ipban VALUES (INET_ATON(${ipBan.ip}), ${ipBan.reason}, $x)")
      case None => data.update(sql"INSERT INTO ipban VALUES (INET_ATON(${ipBan.ip}), ${ipBan.reason}, NULL)")
    }
  }
}