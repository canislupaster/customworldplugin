package com.thomas.customworld.player

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.entity.Player
import com.thomas.customworld.util._
import org.bukkit.ChatColor

package object nick {
  def update (player: Player, nick:Option[String]): Unit =
    nick foreach (x => player.setDisplayName(ChatColor.translateAlternateColorCodes('&', x)))

  def join(player: Player): Unit = {
    update(player, new PlayerDB().getNick(player.getUniqueId))
  }
}
