package scala.com.thomas.customworld

import com.thomas.customworld.CustomCoreJava
import org.bukkit.GameMode

import scala.com.thomas.customworld.commands.home.{DelHomeCommand, HomeTpCommand, SetHomeCommand}
import scala.com.thomas.customworld.commands.mod.ip.{ClearIpsCommand, VerifyIpCommand}
import scala.com.thomas.customworld.commands.mod.ban.{BanCommand, TempBanCommand, UnbanCommand}
import scala.com.thomas.customworld.commands.mod.mute.MuteCommand
import scala.com.thomas.customworld.commands.mod.{JumppadCommand, KickCommand, RankCommand, SmiteCommand}
import scala.com.thomas.customworld.commands.util._
import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import scala.com.thomas.customworld.messaging.Message
import org.bukkit.command.{Command, CommandSender}
import org.bukkit.command.defaults.HelpCommand

import scala.com.thomas.customworld.commands.build._

package object commands {
  def RegisterCommands (plugin: CustomCoreJava, sqldb: DBConstructor): Unit = {
    val cfg = plugin.getConfig

    plugin.getCommand("rank").setExecutor(new RankCommand())

    plugin.getCommand("home").setExecutor(new HomeTpCommand())
    plugin.getCommand("sethome").setExecutor(new SetHomeCommand())
    plugin.getCommand("delhome").setExecutor(new DelHomeCommand())

    plugin.getCommand("spawn").setExecutor(new SpawnCommand(cfg))
    plugin.getCommand("heal").setExecutor(new HealCommand())
    plugin.getCommand("back").setExecutor(new BackCommand())
    plugin.getCommand("world").setExecutor(new WorldCommand(cfg))
    plugin.getCommand("nick").setExecutor(new NickCommand())

    plugin.getCommand("verify").setExecutor(new VerifyIpCommand())
    plugin.getCommand("clearips").setExecutor(new ClearIpsCommand())

    plugin.getCommand("smite").setExecutor(new SmiteCommand())

    plugin.getCommand("help").setExecutor(new commands.HelpCommand())
    plugin.getCommand("rules").setExecutor(new commands.RulesCommand())
    
    plugin.getCommand("ban").setExecutor(new BanCommand())
    plugin.getCommand("tempban").setExecutor(new TempBanCommand())
    plugin.getCommand("unban").setExecutor(new UnbanCommand())
    plugin.getCommand("kick").setExecutor(new KickCommand())

    plugin.getCommand("mute").setExecutor(new MuteCommand(true))
    plugin.getCommand("unmute").setExecutor(new MuteCommand(false))

    plugin.getCommand("jumppad").setExecutor(new JumppadCommand(cfg))
    plugin.getCommand("speed").setExecutor(new SpeedCommand())

    plugin.getCommand("build").setExecutor(new BuildCommand())
    plugin.getCommand("makebuild").setExecutor(new MakeBuildCommand(cfg))
    plugin.getCommand("delbuild").setExecutor(new DelBuildCommand())
    plugin.getCommand("maketheme").setExecutor(new MakeThemeCommand())
    plugin.getCommand("vote").setExecutor(new VoteCommand())
    plugin.getCommand("expandbuild").setExecutor(new ExpandBuildCommand(cfg))
    plugin.getCommand("renamebuild").setExecutor(new RenameBuildCommand())

    plugin.getCommand("gmsp").setExecutor(new GamemodeCommand(GameMode.SPECTATOR))
    plugin.getCommand("gma").setExecutor(new GamemodeCommand(GameMode.ADVENTURE))
    plugin.getCommand("gmc").setExecutor(new GamemodeCommand(GameMode.CREATIVE))
    plugin.getCommand("gms").setExecutor(new GamemodeCommand(GameMode.SURVIVAL))
    plugin.getCommand("clear").setExecutor(new ClearCommand())
  }
}
