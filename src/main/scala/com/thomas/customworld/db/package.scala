package com.thomas.customworld

import java.sql.{Connection, DriverManager, ResultSet}

import org.bukkit.configuration.file.FileConfiguration

import scala.io.Source

package object db {
  def InitializeDB: List[String] = {
    List("""
      |CREATE TABLE IF NOT EXISTS player (
      |    playerid varchar(36) NOT NULL,
      |    rankid INTEGER NOT NULL DEFAULT 0,
      |    nickname varchar(100),
      |    username TEXT NOT NULL,
      |    xp INTEGER NOT NULL DEFAULT 0,
      |    muted BOOL NOT NULL DEFAULT FALSE,
      |    UNIQUE (nickname),
      |    PRIMARY KEY (playerid)
      |);
      |""", """
      |CREATE TABLE IF NOT EXISTS home (
      |    playerid varchar(36) NOT NULL,
      |    homename varchar(600) NOT NULL,
      |    worldid varchar(36) NOT NULL,
      |    x INTEGER NOT NULL,
      |    y INTEGER NOT NULL,
      |    z INTEGER NOT NULL,
      |    FOREIGN KEY (playerid) REFERENCES player(playerid),
      |    UNIQUE (playerid, homename)
      |);
      |
    """, """
      |CREATE TABLE IF NOT EXISTS ip (
      |    playerid varchar(36) NOT NULL,
      |    ip int unsigned NOT NULL,
      |    INDEX(ip),
      |    UNIQUE (playerid, ip),
      |    FOREIGN KEY (playerid) REFERENCES player(playerid)
      |);
    """, """
      |CREATE TABLE IF NOT EXISTS ipban (
      |    ip int unsigned NOT NULL,
      |    reason text NOT NULL,
      |    time datetime,
      |    FOREIGN KEY (ip) REFERENCES ip(ip)
      |);
    """, """
      |CREATE TABLE IF NOT EXISTS mute (
      |    ip int unsigned NOT NULL,
      |    reason text NOT NULL,
      |    time datetime,
      |    FOREIGN KEY (ip) REFERENCES ip(ip)
      |);
    """.stripMargin) map (_.stripMargin)
  }

  type DBConstructor = () => Connection

  def MakeDB (cfg: FileConfiguration): Connection = {
    val hostname = cfg.getString("db.hostname")
    val port = cfg.getString("db.port")
    val dbname = cfg.getString("db.database")
    val user = cfg.getString("db.username")
    val pass = cfg.getString("db.password")

    DriverManager.registerDriver(new com.mysql.jdbc.Driver())
    DriverManager.getConnection(s"jdbc:mysql://$hostname:$port/$dbname?autoReconnect=true&useSSL=false", user, pass)
  }

}