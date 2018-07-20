package com.thomas.customworld

import com.thomas.customworld.db.Home
import com.thomas.customworld.rank.Rank
import org.bukkit.command.CommandSender
import org.bukkit.{ChatColor, Server}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

package object messaging {
  var langcfg:Any = null

  def LoadMessage (msg :String): String = {
    langcfg match {
      case x:FileConfiguration =>
        x.getString(s"lang.$msg")
      case _ => "Configuration has not loaded yet!"
    }
  }

  trait Message {
    def premsg: String = LoadMessage("prefix") + " "

    def renderMessage: String = this match {
      case ErrorMsg (err) =>
        premsg + "§c" + LoadMessage(err)
      case SuccessMsg () =>
        premsg + "§a" + LoadMessage("success")
      case InfoMsg (msg, None) =>
        premsg + "§e" + LoadMessage(msg)
      case InfoMsg (msg, Some(aff)) =>
        s"§e${LoadMessage(msg)} $aff"
      case InfoMsgRev (msg, pre) =>
        s"§e$pre ${LoadMessage(msg)}"
      case HomeMessage (Some(msg), Home(name,world,x,y,z)) =>
        s"§e${LoadMessage(msg)} $name: $x,$y,$z"
      case HomeMessage (None, Home(name,world,x,y,z)) =>
        s"§e$name: $x,$y,$z"
      case PlayerMessage (tag, username, msg) =>
        s"§b[${tag.Tag}§b] §7$username: §f$msg"
      case PlayerJoinMessage (join, tag, username) =>
        s"§7[§b${if (join) {"+"} else {"-"}}§7] §1[${tag.Tag}§1] §7$username"
    }

    def sendClient (client:CommandSender): Unit = {
      client.sendMessage(this.renderMessage)
    }

    def broadCast (playerfilter: Player => Boolean, server :Server): Unit = {
      server.getOnlinePlayers.toArray filter ((x:AnyRef) => playerfilter(x.asInstanceOf[Player])) foreach ((x:AnyRef) => this.sendClient(x.asInstanceOf[Player]))
    }
  }

  case class ErrorMsg(err :String) extends Message
  case class SuccessMsg() extends Message
  case class InfoMsg (msg :String, affix:Option[String]) extends Message
  case class InfoMsgRev (msg :String, prefix:String) extends Message

  case class PlayerMessage (tag :Rank, username:String, msg :String) extends Message
  case class PlayerJoinMessage (join:Boolean, tag :Rank, username :String) extends Message

  case class HomeMessage (msg:Option[String], home: Home) extends Message

  def LoadConfig (config: FileConfiguration): Unit = {
    langcfg = config
  }
}
