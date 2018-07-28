package com.thomas.customworld.db

import com.github.takezoe.scala.jdbc._
import com.thomas.customworld.commands.home.Home


class HomeDB() extends MainDB() {
  def GetHomes (userid:String) : Array[Home]  = {
    val s = data.select(sql"SELECT * FROM home WHERE playerid=$userid") { x => Home(x.getString("homename"), x.getString("worldid"), x.getInt("x"), x.getInt("y"), x.getInt("z"))}
    s.toArray
  }

  def GetNumHomes (userid:String): Integer = {
    val Some(num:Int) = data.selectFirst(sql"SELECT Count(*) FROM home WHERE playerid=$userid"){x => x.getInt("Count(*)")}
    num
  }

  def SetHome (userid:String, home:Home): Unit = {
    val Home(name, world, x, y, z) = home
    data.update(sql"REPLACE INTO home VALUES ($userid, $name, $world, $x, $y, $z)")
  }

  def DelHome (userid:String, homename:String): Boolean = {
    data.update (sql"DELETE FROM home WHERE homename=$homename AND playerid=$userid") match {case 0 => false; case 1 => true}
  }
}
