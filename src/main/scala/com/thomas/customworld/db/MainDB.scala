package scala.com.thomas.customworld.db

import java.sql.Connection
import java.util.UUID

import com.github.takezoe.scala.jdbc.DB
import scala.com.thomas.customworld.CustomCore

class MainDB () {
  val data = DB (CustomCore.dbcons())

  def close(): Unit = {
    data.close()
  }

  def autoClose[A](f: this.type => A): A = {
    val x = f(this)
    close()
    x
  }
}
