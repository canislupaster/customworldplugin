package com.thomas.customworld

import java.sql.{Connection, DriverManager, ResultSet}

import org.bukkit.configuration.file.FileConfiguration
import org.postgresql.PGConnection

import scala.reflect.ClassTag

package object db {
  def InitializeDB: String =
    """
      |CREATE TABLE IF NOT EXISTS player (
      |    playerid TEXT NOT NULL,
      |    rankid INTEGER NOT NULL,
      |     username TEXT NOT NULL,
      |     muted BOOL NOT NULL DEFAULT FALSE,
      |    PRIMARY KEY (playerid)
      |);
      |
      |CREATE TABLE IF NOT EXISTS home (
      |    playerid TEXT NOT NULL,
      |    homename TEXT NOT NULL,
      |    worldid TEXT NOT NULL,
      |    x INTEGER NOT NULL,
      |    y INTEGER NOT NULL,
      |    z INTEGER NOT NULL,
      |    FOREIGN KEY (playerid) REFERENCES player(playerid),
      |    UNIQUE (playerid, homename)
      |);
    """.stripMargin

  type DBConstructor = () => Connection

  def MakeDB (cfg: FileConfiguration): Connection = {
    val hostname = cfg.getString("db.hostname")
    val port = cfg.getString("db.port")
    val dbname = cfg.getString("db.database")
    val user = cfg.getString("db.username")
    val pass = cfg.getString("db.password")
    DriverManager.registerDriver(new org.postgresql.Driver())
    DriverManager.getConnection(s"jdbc:postgresql://localhost:5432/customworld", user, pass)
  }

}