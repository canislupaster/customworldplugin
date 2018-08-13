package scala.com.thomas.customworld.player

import scala.com.thomas.customworld.player._
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.{Cancellable, Event}

import scala.com.thomas.customworld.CustomCore.GetTag
import scala.com.thomas.customworld.EventModule
import scala.com.thomas.customworld.messaging.PlayerJoinMessage
import scala.com.thomas.customworld.utility.toUUID

object playerEventModule extends EventModule {
  override def playerEv(event: Event, player: Player): Unit = {
    val cplayer = getPlayer(player)
    cplayer.playerPerms.playerEv(event, player)
  }

  override def ev(event: Event): Unit = players foreach (_._2.playerPerms.ev(event))
  override def join(player: Player): Unit = {
    val customplayer = new CustomWorldPlayer(player)
    players += (toUUID(player.getUniqueId) -> customplayer)

    players foreach (_._2.playerPerms.join(player))
  }
  override def leave(player: Player): Unit = {
    players foreach (_._2.playerPerms.leave(player))

    getPlayer(player) leave()
    players -= player.getUniqueId
  }
  override def reset(player: Player): Unit = players foreach (_._2.playerPerms.reset(player))
}
