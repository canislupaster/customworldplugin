package scala.com.thomas.customworld

import java.time.OffsetTime
import java.util.{Calendar, Date}

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.{GameMode, Location}
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin

import scala.com.thomas.customworld.CustomCore._
import scala.com.thomas.customworld.player.freeop.FreeOPPlayer
import scala.com.thomas.customworld.messaging.{ConfigMsg, ErrorMsg, InfoMsg}
import scala.com.thomas.customworld.minigame.{Minigame, MinigamePlayerData}
import scala.com.thomas.customworld.player.rank.{Muted, Rank, Regular}
import scala.com.thomas.customworld.utility._
import org.bukkit.event.{Cancellable, Event}

import scala.collection.mutable
import scala.com.thomas.customworld.event.EventModule

package object player {
  case class CustomPlayer(playerid:UUID, username:String, rank:Rank, nickname:Option[String])

  trait PlayerType extends EventModule {def extraPerms:Set[String] = Set();}
  case class MinigamePlayer[A <: MinigamePlayerData](minigame:Minigame[A]) extends PlayerType {
    override def playerEv(event: Event, player: Player): Unit = minigame.playerEv(event, player)

    override def join(player: Player): Unit = minigame.join(player)
    override def leave(player: Player): Unit = minigame.leave(player)
  }

  case object UnverifiedPlayer extends PlayerType {
    override def playerEv(event: Event, player: Player): Unit = {
      event match {
        case _:AsyncPlayerChatEvent => ()
        case event:Cancellable => event.setCancelled(true)
      }
    }
  }

  case object SurvivalPlayer extends PlayerType

  trait TpMode
  case object TpFrom
  case object TpTo
  case class TpRequest (tpMode: TpMode, time:Date, player: Player) {} //TODO: EXPIRED AND CMDS

  case class CustomWorldPlayer (rankcons: Rank, permissionAttachment: PermissionAttachment) {
    var rank: Rank = Regular
    var playerPerms:PlayerType = UnverifiedPlayer

    var beforeTp: Option[Location] = None
    var tpaRequest: Option[TpRequest] = None

    def verify (player:Player): Unit = {
      joinFreeOP(player)
      updateRank(rankcons)
    }

    def reset (player:Player): Unit = {
      player.setGameMode(GameMode.CREATIVE)
      player.setCollidable(false)
      player.setHealth(20)
      player.setFlySpeed(0.1f)
      player.setWalkSpeed(0.2f)
    }

    def updatePermissions (player:Player, newp: PlayerType): Unit = {
      if (playerPerms != null) {
        playerPerms.leave(player)
        playerPerms.extraPerms foreach permissionAttachment.unsetPermission
      } //waw a null check how awful
      playerPerms = newp
      reset (player)
      newp.join(player)
      playerPerms.extraPerms foreach (permissionAttachment.setPermission(_, true))
    }

    def updateRank (newrank: Rank): Unit = {
      val newerrank = rank copy newrank
      rank.addPermissions foreach permissionAttachment.unsetPermission
      rank.subPermissions foreach permissionAttachment.unsetPermission //TODO: HAELP
      newerrank.addPermissions foreach (permissionAttachment.setPermission(_,true))
      newerrank.subPermissions foreach (permissionAttachment.setPermission(_,false))
      rank = newerrank
    }

    def leave(): Unit = {

    }

    def this (player: Player) {
      this(new PlayerDB().autoClose(db => db.getPlayer(player.getUniqueId).rank), player.addAttachment(plugin))
    }
  }

  var players: mutable.HashMap[UUID, CustomWorldPlayer] = mutable.HashMap()
  def getPlayer (player: Player): CustomWorldPlayer = players(player.getUniqueId)

  def joinMinigames[A <: MinigamePlayerData] (player: Player, minigame:Minigame[A]): Unit = {
    val u = player.getUniqueId
    players(u).updatePermissions(player, MinigamePlayer(minigame))
  }

  def joinSurvival (player: Player): Unit = {
    val u = player.getUniqueId
    players(u).updatePermissions(player, SurvivalPlayer)
  }

  def joinFreeOP (player: Player): Unit = {
    val u = player.getUniqueId
    players(u).updatePermissions(player, FreeOPPlayer(configuration.cfg))
    InfoMsg(ConfigMsg("opped")) sendClient player
  }
}
