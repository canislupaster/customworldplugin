package scala.com.thomas.customworld.player.nick

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.entity.Player

import scala.com.thomas.customworld.utility._
import org.bukkit.ChatColor

import scala.com.thomas.customworld.commands.base
import scala.com.thomas.customworld.event.EventModule

object nickEventModule extends EventModule {
  def update (player: Player, nick:Option[String]): Unit =
    nick match {
      case Some(n) => player.setDisplayName(ChatColor.translateAlternateColorCodes('&', n))
      case None => player.setDisplayName(base.stripName(player.getName))
    }

  override def join(player: Player): Unit = {
    update(player, new PlayerDB().autoClose(x => x.getPlayer(player.getUniqueId)).nickname)
  }
}
