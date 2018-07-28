package com.thomas.customworld

import com.thomas.customworld.commands.home.{DelHomeCommand, HomeCommand, SetHomeCommand}
import com.thomas.customworld.commands.mod.ip.{ClearIpsCommand, VerifyIpCommand}
import com.thomas.customworld.commands.mod.ban.{BanCommand, TempBanCommand, UnbanCommand}
import com.thomas.customworld.commands.mod.mute.MuteCommand
import com.thomas.customworld.commands.mod.{JumppadCommand, KickCommand, RankCommand, SmiteCommand}
import com.thomas.customworld.commands.util._
import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.command.defaults.HelpCommand

package object commands {

  def RegisterCommands (plugin: CustomWorldPluginJava, sqldb: DBConstructor): Unit = {
    val cfg = plugin.getConfig

    plugin.getCommand("rank").setExecutor(new RankCommand())

    plugin.getCommand("home").setExecutor(new HomeCommand())
    plugin.getCommand("sethome").setExecutor(new SetHomeCommand())
    plugin.getCommand("delhome").setExecutor(new DelHomeCommand())

    plugin.getCommand("spawn").setExecutor(new SpawnCommand())
    plugin.getCommand("heal").setExecutor(new HealCommand())
    plugin.getCommand("back").setExecutor(new BackCommand())
    plugin.getCommand("world").setExecutor(new WorldCommand(cfg.getString("world.overworld"), cfg.getString("world.flatlands")))
    plugin.getCommand("nick").setExecutor(new NickCommand())

    plugin.getCommand("verify").setExecutor(new VerifyIpCommand())
    plugin.getCommand("clearips").setExecutor(new ClearIpsCommand())

    plugin.getCommand("smite").setExecutor(new SmiteCommand())

    plugin.getCommand("help").setExecutor(new commands.HelpCommand())

    plugin.getCommand("ban").setExecutor(new BanCommand())
    plugin.getCommand("tempban").setExecutor(new TempBanCommand())
    plugin.getCommand("unban").setExecutor(new UnbanCommand())
    plugin.getCommand("kick").setExecutor(new KickCommand())

    plugin.getCommand("mute").setExecutor(new MuteCommand(true))
    plugin.getCommand("unmute").setExecutor(new MuteCommand(false))

    plugin.getCommand("jumppad").setExecutor(new JumppadCommand(plugin.getConfig))
    //TODO: SPEED
  }
}
