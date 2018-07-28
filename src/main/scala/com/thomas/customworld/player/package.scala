package com.thomas.customworld

import java.time.OffsetTime
import java.util.{Calendar, Date, UUID}

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.{GameMode, Location}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin
import com.thomas.customworld.CustomWorldPlugin._
import com.thomas.customworld.messaging.{ConfigMsg, InfoMsg}
import com.thomas.customworld.player.rank.{Muted, Rank, Regular}
import com.thomas.customworld.util._

import scala.collection.mutable

package object player {
  trait PlayerType {def extraPerms:Set[String] = Set()}
  case object FreeOPPlayer extends PlayerType {
    override def extraPerms: Set[String] = rank.rankCfg("freeop")
  }
  case object MinigamePlayer extends PlayerType

  trait TpMode
  case object TpFrom
  case object TpTo
  case class TpRequest (tpMode: TpMode, time:Date, player: Player) {} //TODO: EXPIRED AND CMDS

  case class CustomWorldPlayer (rankcons: Rank, permissionAttachment: PermissionAttachment) {
    var rank: Rank = Regular
    var playerPerms:PlayerType = FreeOPPlayer

    var verified = false
    var beforeTp: Option[Location] = None
    var tpaRequest: Option[TpRequest] = None

    def verify (): Unit = {
      updateRank(rankcons)
      verified = true
    }

    def updatePermissions (newp: PlayerType): Unit = {
      playerPerms.extraPerms foreach permissionAttachment.unsetPermission
      playerPerms = newp
      playerPerms.extraPerms foreach (permissionAttachment.setPermission(_, true))
    }

    def updateRank (newrank: Rank): Unit = {
      newrank match {
        case Muted =>
          Muted.permissions foreach (permissionAttachment.setPermission(_, false))
        case x:Rank =>
          rank.permissions foreach permissionAttachment.unsetPermission
          x.permissions foreach (permissionAttachment.setPermission(_,true))
      }
      rank = newrank
    }

    def leave(): Unit = {

    }


    def this (player: Player) {
      this(new PlayerDB().autoClose(db => db.getRank(player.getUniqueId)), player.addAttachment(plugin))
    }
  }

  var players: mutable.HashMap[UUID, CustomWorldPlayer] = mutable.HashMap()

  def getPlayer (player: Player): CustomWorldPlayer = players(player.getUniqueId)
  def isFreeOP (player: Player): Boolean = getPlayer(player).playerPerms == FreeOPPlayer

  def joinMinigames (player: Player): Unit = {
    val u = player.getUniqueId
    players(u).updatePermissions(MinigamePlayer)
  }

  def joinFreeOP (player: Player): Unit = {
    val u = player.getUniqueId
    minigame.leave (player)
    players(u).updatePermissions(FreeOPPlayer)
    player.setGameMode(GameMode.CREATIVE)
    InfoMsg(ConfigMsg("opped")) sendClient player
  }

  def join (player: Player): Unit = {
    players += (player.getUniqueId -> new CustomWorldPlayer(player))
    ip.join(player)
    nick.join(player)
    mute.join(player)
    joinFreeOP(player)
  }

  def leave (player: Player): Unit = {
    players(player.getUniqueId) leave()
    players -= player.getUniqueId
  }
}
