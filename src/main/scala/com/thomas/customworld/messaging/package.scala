package com.thomas.customworld

import com.thomas.customworld.commands.home.Home
import com.thomas.customworld.player.rank.Rank
import org.bukkit.command.CommandSender
import org.bukkit.{ChatColor, Server}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import com.thomas.customworld.util._

package object messaging {
  var langcfg:Any = _

  def LoadMessage (msg :String): String = {
    langcfg match {
      case x:FileConfiguration =>
        x.getString(s"lang.$msg")
      case _ => "Configuration has not loaded yet!"
    }
  }

  trait MsgType
  case class ConfigMsg(msg:String) extends MsgType
  case class RuntimeMsg(msg:String) extends MsgType

  def ParseMsgTypes (msgType: List[MsgType]) :List[String] = msgType match {
    case ConfigMsg(x)::y => LoadMessage(x)::ParseMsgTypes(y)
    case RuntimeMsg(x)::y => x::ParseMsgTypes(y)
    case _ => List()
  }

  trait Message {
    def premsg: String = LoadMessage("prefix") + " "

    def renderMessage: String =
      this match {
        case ErrorMsg (err) =>
          premsg + "§c" + LoadMessage(err)
        case SuccessMsg =>
          premsg + "§a" + LoadMessage("success")
        case x:InfoMsg =>
          premsg + "§e" + spaceJoin (ParseMsgTypes(x.list))
        case HomeMessage (Some(msg), Home(name,world,x,y,z)) =>
          s"§e${LoadMessage(msg)} $name: $x,$y,$z"
        case HomeMessage (None, Home(name,world,x,y,z)) =>
          s"§e$name: $x,$y,$z"
        case PlayerMessage (tag, username) =>
          val name = if (username.getDisplayName == username.getName) {"%s"} else s"%s §8aka ${username.getName}"
          s"§b[${tag.Tag}§b] §7$name§7: §f%s"
        case PlayerJoinMessage (join, tag, username) =>
          s"§7[§b${if (join) {"+"} else {"-"}}§7] §1[${tag.Tag}§1] §7$username"
        case CommandMessage (u, cmd) =>
          s"$premsg§e$u ${LoadMessage("hasexec")} $cmd"

        case PunishMsg (punished, punisher, punishment, reason) => premsg + s"§c${punished.getName} was $punishment by ${punisher.getName} for §e$reason"
      }

    def sendClient (client:CommandSender): Unit = {
      client.sendMessage(this.renderMessage.split("\n"))
    }

    def broadCast (playerfilter: Player => Boolean)(server :Server): Unit = {
      this.sendClient (server.getConsoleSender)
      server.getOnlinePlayers.toArray filter ((x:AnyRef) => playerfilter(x.asInstanceOf[Player])) foreach ((x:AnyRef) => this.sendClient(x.asInstanceOf[Player]))
    }

    def globalBroadcast: Server => Unit = broadCast(x => true)
  }

  case class ErrorMsg(err :String) extends Message
  case object SuccessMsg extends Message
  case class InfoMsg (msg :MsgType*) extends Message { val list: List[MsgType] = msg.toList }

  case class PlayerMessage (tag :Rank, player :Player) extends Message
  case class PlayerJoinMessage (join:Boolean, tag :Rank, username :String) extends Message

  case class HomeMessage (msg:Option[String], home: Home) extends Message

  case class CommandMessage (username: String, cmd: String) extends Message
  case class PunishMsg(punished: Player, punisher: CommandSender, punishment:String, reason:String) extends Message

  def LoadConfig (config: FileConfiguration): Unit = {
    langcfg = config
  }
}
