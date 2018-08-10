package scala.com.thomas.customworld.player.nick

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.util._
import org.bukkit.ChatColor

import scala.com.thomas.customworld.EventModule

object nickEventModule extends EventModule {
  def update (player: Player, nick:Option[String]): Unit =
    nick foreach (x => player.setDisplayName(ChatColor.translateAlternateColorCodes('&', x)))

  override def join(player: Player): Unit = {
    update(player, new PlayerDB().autoClose(x => x.getPlayer(player.getUniqueId)).nickname)
  }
}
