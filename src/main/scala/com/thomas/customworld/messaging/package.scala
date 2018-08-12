package scala.com.thomas.customworld

import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}

import net.md_5.bungee.api.chat._

import scala.com.thomas.customworld.commands.home.Home
import scala.com.thomas.customworld.player.rank.Rank
import org.bukkit.command.CommandSender
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import net.md_5.bungee.api.ChatColor._
import sx.blah.discord.handle.obj.{IChannel, IMessage}

import scala.com.thomas.customworld.commands.base
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

    def renderMessage: ComponentBuilder = {
      val pre = new ComponentBuilder(premsg)
      val newln = new ComponentBuilder((premsg map (_ => " ") mkString) + " ")
      val blank = new ComponentBuilder("")
      this match {
        case ErrorMsg(err) =>
          pre.append(LoadMessage(err)).color(RED)
        case SuccessMsg =>
          premsg + "§a" + LoadMessage("success")
          pre.append(LoadMessage("success")).color(GREEN)
        case x: InfoMsg =>
          x.list.foldRight(pre)((x,y) => y.append(x.renderMessage.color(YELLOW).create()))
        case HomeMessage(Home(name, world, x, y, z)) =>
          new ComponentBuilder("$name: $x,$y,$z").color(YELLOW)
        case PlayerMessage(tag, username) =>
          val name = if (username.getDisplayName == username.getName) "%s" else s"%s §8aka ${username.getName}"

          blank.append("[").color(AQUA).append(tag.Tag).append("] ").color(AQUA)
          .append(name).color(GOLD).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(username.getName).create()))
          .append(": ").color(GRAY).append("%s").color(WHITE)
        case PlayerJoinMessage(join, tag, username) =>
          val joinpre = if (join) "+" else "-"
          pre.append(joinpre).color(YELLOW).append("[").color(AQUA).append(tag.Tag).append("]").color(AQUA).append(username)
        case CommandMessage(u, cmd) =>
          pre.append(u).color(GOLD).append(" has executed ").color(YELLOW).append(cmd).color(GOLD)
        case BuildMessage(build) =>
          val Build(_, pid, _, name, theme, created) = build
          val uname = new PlayerDB().autoClose(_.getPlayer(pid)).username
          val points = new BuildDB().autoClose(_.getVotes(build)) map (_._2)
          val themecomponent = theme match {
              case Some(x) => new ComponentBuilder(s" for ").append(x.name).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list ${x.name}")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out other builds for this theme!").create()))
              case None => new ComponentBuilder(" ")
            }
          newln.append(build.buildName)
            .append("made by ").color(YELLOW)
            .append(uname).color(GOLD).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list $uname")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out his other builds!").create()))
            .append(themecomponent.create()).color(GOLD)
            .append(" at ").color(YELLOW).append(dateformat(build.timeCreated)).color(GOLD)
            .append(": ").color(YELLOW).append(points.sum.toString).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s"/vote 5 $uname ${build.buildName}")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote!").create()))
        case ThemeMessage(Theme(_, name, starting, ending)) =>
          val color = if (Now().after(ending)) DARK_GRAY else YELLOW
          newln.append(s"$name: ${dateformat(starting)} - ${dateformat(ending)}").color(color).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list $name")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out builds for this theme!").create()))
        case PunishMsg(punished, punisher, punishment, reason) =>
          pre.append(punished.getName).color(GOLD).append(s" was $punishment by ${punisher.getName} for ").color(YELLOW).append(reason).color(YELLOW)
        case PageMsg(Paginator(x, _)) => pre.append(LoadMessage("page")).color(YELLOW).append(": ").color(YELLOW).append(x.toString).color(GOLD)


        case ConfigMsg(x) => new ComponentBuilder(LoadMessage(x))
        case RuntimeMsg(x) => new ComponentBuilder(x)
      }
    }

    def discordMessage (channel:IChannel): IMessage = {
      channel.sendMessage(plainText)
    }


    def sendClient (client:CommandSender): Unit = {
      val msg = this.renderMessage.create() reduce ((x,y) => {x.addExtra(y); x})
      client.spigot().sendMessage(msg)
    }

    def formattedText = {
      this.renderMessage.create().map(_.toLegacyText).mkString
    }

    def plainText = {
      this.renderMessage.create().map(_.toPlainText).mkString
    }

    def broadCast (playerfilter: Player => Boolean)(server :Server): Unit = {
      this.sendClient (server.getConsoleSender)
      server.getOnlinePlayers.toArray filter ((x:AnyRef) => playerfilter(x.asInstanceOf[Player])) foreach ((x:AnyRef) => this.sendClient(x.asInstanceOf[Player]))
    }

    def globalBroadcast: Server => Unit = broadCast(_ => true)
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
