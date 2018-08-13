package scala.com.thomas.customworld

import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.core.entities.MessageChannel
import net.md_5.bungee.api.chat._

import scala.com.thomas.customworld.commands.home.Home
import scala.com.thomas.customworld.player.rank.Rank
import org.bukkit.command.CommandSender
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import net.md_5.bungee.api.ChatColor._

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.commands.build.{Build, Theme}
import scala.com.thomas.customworld.db.{BuildDB, PlayerDB}
import scala.com.thomas.customworld.discord.DiscordCommandSender
import scala.com.thomas.customworld.utility._

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
  def premsg: String = LoadMessage("prefix") + " "
  def plainpremsg = new TextComponent(premsg).toPlainText

  trait Message {

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
          x.list.foldLeft(pre)((y,x) => y.append(x.renderMessage.color(YELLOW).append(" ").create()))
        case HomeMessage(Home(name, world, x, y, z)) =>
          blank.append(s"$name: $x,$y,$z").color(YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/home $name")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport!").create()))
        case PlayerMessage(tag, username, msg) =>
          val name = if (username.getDisplayName == base.stripName(username.getName))
            new ComponentBuilder({username.getDisplayName}) else
            new ComponentBuilder(username.getDisplayName).append(s" aka ${username.getName}").color(DARK_GRAY)

          blank.append("[").color(AQUA).append(tag.Tag).append("] ").color(AQUA)
          .append(name.create()).color(GOLD).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(username.getName).create()))
          .append(": ").color(GRAY).append(msg).color(WHITE)
        case PlayerJoinMessage(join, tag, username) =>
          val joinpre = if (join) "+" else "-"
          blank.append(joinpre).color(YELLOW).append(" [").color(AQUA).append(tag.Tag).append("] ").color(AQUA).append(username).color(GOLD)
        case CommandMessage(u, cmd) =>
          pre.append(u).color(GOLD).append(" has executed ").color(YELLOW).append(cmd).color(GOLD)
        case BuildMessage(build) =>
          val Build(_, pid, _, name, theme, created) = build
          val uname = new PlayerDB().autoClose(_.getPlayer(pid)).username
          val points = new BuildDB().autoClose(_.getVotes(build)) map (_._2)
          val themecomponent = theme match {
              case Some(x) => new ComponentBuilder(s" for ").color(YELLOW).append(x.name).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list ${x.name}")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out other builds for this theme!").create()))
                                .append(" ")
              case None => new ComponentBuilder(" ")
            }
          blank.append(build.buildName).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build tp $uname ${build.buildName}")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport to this build!").create()))
            .append(" made by ").color(YELLOW)
            .append(uname).color(GOLD).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list $uname")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out his other builds!").create()))
            .append(themecomponent.create()).color(GOLD)
            .append("at ").color(YELLOW).append(dateformat(build.timeCreated)).color(GOLD)
            .append(": ").color(YELLOW).append(points.sum.toString).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s"/vote 5 $uname ${build.buildName}")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote!").create()))
        case ThemeMessage(Theme(_, name, starting, ending)) =>
          val color = if (Now().after(ending)) DARK_GRAY else YELLOW
          blank.append(s"$name: ${dateformat(starting)} - ${dateformat(ending)}").color(color).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s"/build list $name")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to check out builds for this theme!").create()))
        case PunishMsg(punished, punisher, punishment, reason) =>
          pre.append(punished.getName).color(GOLD).append(s" was $punishment by ${punisher.getName} for ").color(YELLOW).append(reason).color(YELLOW)
        case PageMsg(Paginator(x, _)) => pre.append(LoadMessage("page")).color(YELLOW).append(": ").color(YELLOW).append(x.toString).color(GOLD)


        case ConfigMsg(x) => new ComponentBuilder(LoadMessage(x))
        case RuntimeMsg(x) => new ComponentBuilder(x)
      }
    }

    def discordMessage (channel:MessageChannel): Unit = {
      channel.sendMessage(this.plainText stripPrefix plainpremsg).queue()
    }


    def sendClient (client:CommandSender): Unit = {
      val msg = this.renderMessage.create() reduce ((x,y) => {x.addExtra(y); x})
      client match {
        case x:DiscordCommandSender => discordMessage(x.getChannel)
        case _ => client.spigot().sendMessage(msg)
      }
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

    def globalBroadcast: Server => Unit = {
      DiscordSRV.getPlugin.getMainTextChannel match {case null => (); case x => discordMessage(x)}
      broadCast(_ => true)
    }
  }

  case class ConfigMsg(msg:String) extends Message
  case class RuntimeMsg(msg:String) extends Message

  case class ErrorMsg(err :String) extends Message
  case object SuccessMsg extends Message
  case class InfoMsg (msg :Message*) extends Message { val list: List[Message] = msg.toList }

  case class PlayerMessage (tag :Rank, player :Player, msg:String) extends Message
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
