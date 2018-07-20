package com.thomas.customworld

import com.thomas.customworld.commands.home.{DelHomeCommand, HomeCommand, SetHomeCommand}
import com.thomas.customworld.db.{DBConstructor, PlayerDB}

package object commands {

  def RegisterCommands (plugin: CustomWorldPluginJava, sqldb: DBConstructor): Unit = {
    plugin.getCommand("rank").setExecutor(new RankCommand(sqldb))

    plugin.getCommand("home").setExecutor(new HomeCommand(sqldb))
    plugin.getCommand("sethome").setExecutor(new SetHomeCommand(sqldb))
    plugin.getCommand("delhome").setExecutor(new DelHomeCommand(sqldb))

    plugin.getCommand("spawn").setExecutor(new SpawnCommand())
    //TODO: heal cmd
    //TODO: SPEED
  }
}
