package scala.com.thomas.customworld

import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}

import scala.com.thomas.customworld.commands.home.Home
import scala.com.thomas.customworld.player.rank.Rank
import org.bukkit.command.CommandSender
import org.bukkit.{ChatColor, Server}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import sx.blah.discord.handle.obj.{IChannel, IMessage}

import scala.com.thomas.customworld.commands.build.{Build, Theme}
import scala.com.thomas.customworld.db.{BuildDB, PlayerDB}
import scala.com.thomas.customworld.util._

package object messaging {
  var langcfg:Any = _

  def LoadMessage (msg :String): String = {
    langcfg match {
      case x:FileConfiguration =>
        x.getString(s"lang.$msg")
      case _ => "Configuration has not loaded yet!"
    }
  }

  def dateformat(time:Timestamp): String = new SimpleDateFormat("MMM dd, yyyy").format(time)

  trait Message {
    def premsg: String = LoadMessage("prefix") + " "

    def renderMessage: String =
      this match {
        case ErrorMsg (err) =>
          premsg + "§c" + LoadMessage(err)
        case SuccessMsg =>
          premsg + "§a" + LoadMessage("success")
        case x:InfoMsg =>
          premsg + "§e" + spaceJoin (x.list.map (_.renderMessage))
        case HomeMessage (Home(name,world,x,y,z)) =>
          s"§e$name: $x,$y,$z"
        case PlayerMessage (tag, username) =>
          val name = if (username.getDisplayName == username.getName) {"%s"} else s"%s §8aka ${username.getName}"
          s"§b[${tag.Tag}§b] §7$name§7: §f%s"
        case PlayerJoinMessage (join, tag, username) =>
          s"§7[§b${if (join) {"+"} else {"-"}}§7] §1[${tag.Tag}§1] §7$username"
        case CommandMessage (u, cmd) =>
          s"$premsg§e$u ${LoadMessage("hasexec")} $cmd"
        case BuildMessage (build) =>
          val Build(_, pid, _, name, theme, created) = build
          val uname = new PlayerDB().autoClose(_.getPlayer(pid)).username
          val points = new BuildDB().autoClose(_.getVotes(build))
          s"$name§e made by §c$uname${theme match {case Some (x) => s"§e for §c${x.name} "; case None => " "}}§eat §c${dateformat(created)}§e: §c${points.length}"
        case ThemeMessage (Theme(_, name, starting, ending)) =>
          val color = if(Now().after(ending)) ChatColor.DARK_GRAY else ChatColor.YELLOW
          s"$color$name: ${dateformat(starting)} - ${dateformat(ending)}"
        case PunishMsg (punished, punisher, punishment, reason) => premsg + s"§c${punished.getName} was $punishment by ${punisher.getName} for §e$reason"
        case PageMsg(Paginator(x, _)) => s"$premsg§e${LoadMessage("page")}: §e$x"

        case ConfigMsg(x) => LoadMessage(x)
        case RuntimeMsg(x) => x
      }

    def discordMessage (channel:IChannel): IMessage = {
      val newmsg = renderMessage replaceAllLiterally(premsg, "") replaceAll("§\\w", "")
      channel.sendMessage(newmsg)
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

  case class ConfigMsg(msg:String) extends Message
  case class RuntimeMsg(msg:String) extends Message

  case class ErrorMsg(err :String) extends Message
  case object SuccessMsg extends Message
  case class InfoMsg (msg :Message*) extends Message { val list: List[Message] = msg.toList }

  case class PlayerMessage (tag :Rank, player :Player) extends Message
  case class PlayerJoinMessage (join:Boolean, tag :Rank, username :String) extends Message

  case class HomeMessage (home: Home) extends Message
  case class BuildMessage (build: Build) extends Message
  case class ThemeMessage (theme: Theme) extends Message

  case class CommandMessage (username: String, cmd: String) extends Message
  case class PunishMsg(punished: Player, punisher: CommandSender, punishment:String, reason:String) extends Message
  case class PageMsg (page:Paginator) extends Message

  def LoadConfig (config: FileConfiguration): Unit = {
    langcfg = config
  }
}
