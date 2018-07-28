package com.thomas.customworld.db

import java.sql.Connection
import java.util.UUID

import com.github.takezoe.scala.jdbc.DB
import com.thomas.customworld.CustomWorldPlugin

class MainDB () {
  val data = DB (CustomWorldPlugin.dbcons())

  def close(): Unit = {
    data.close()
  }

  def autoClose[A](f: this.type => A): A = {
    val x = f(this)
    close()
    x
  }
}
