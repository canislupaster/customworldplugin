package com.thomas.customworld.db

import java.sql.Connection

import com.github.takezoe.scala.jdbc.DB

class MainDB (conn:Connection) {
  val data = DB (conn)

  def close(): Unit = {
    data.close()
  }

  def autoClose[A](f: this.type => A): A = {
    val x = f(this)
    close()
    x
  }
}
