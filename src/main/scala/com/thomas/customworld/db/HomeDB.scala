package com.thomas.customworld.db

import java.sql.Connection
import java.util.UUID

import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.util._

import scala.collection.JavaConverters._

case class Home(Name: String, World: UUID, X: Int, Y: Int, Z: Int)

class HomeDB(conn:Connection) extends MainDB(conn) {
  def GetHomes (userid:UUID) : Array[Home]  = {
    val s = data.select(sql"SELECT * FROM home WHERE playerid=${userid.toString}") {x => Home(x.getString("homename"), UUID.fromString(x.getString("worldid")), x.getInt("x"), x.getInt("y"), x.getInt("z"))}
    s.toArray
  }

  def GetNumHomes (userid:UUID): Integer = {
    val Some(num:Int) = data.selectFirst(sql"SELECT Count(*) FROM home WHERE playerid=${userid.toString}"){x => x.getInt("Count(*)")}
    num
  }

  def SetHome (userid:UUID, home:Home): Unit = {
    val Home(name, world, x, y, z) = home
    data.update(sql"REPLACE INTO home VALUES (${userid.toString}, $name, ${world.toString}, $x, $y, $z)")
  }

  def DelHome (userid:UUID, homename:String): Boolean = {
    data.update (sql"DELETE FROM home WHERE homename=$homename") match {case 0 => false; case 1 => true}
  }
}
