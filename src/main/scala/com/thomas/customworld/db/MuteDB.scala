package com.thomas.customworld.db

import java.sql.{Connection, Date, Timestamp}
import java.util.UUID

import com.thomas.customworld.util._
import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.player.mute.Mute


class MuteDB() extends MainDB() {
  def getMutes (ip:String): Seq[Mute] = {
    data.select(sql"SELECT * FROM mute WHERE ip=INET_ATON($ip) AND (time IS NULL OR time > NOW())") {x => Mute(ip, Option(x.getTimestamp("time")), x.getString("reason"))}
  }

  def removeMute (ip:String): Int = {
    data.update(sql"DELETE FROM mute WHERE ip=INET_ATON($ip)")
  }

  def addMute (mute: Mute): Int = {
    mute.time match {
      case Some(x) => data.update(sql"INSERT INTO mute VALUES (INET_ATON(${mute.ip}), ${mute.reason}, $x)")
      case None => data.update(sql"INSERT INTO mute VALUES (INET_ATON(${mute.ip}), ${mute.reason}, NULL)")
    }
  }
}