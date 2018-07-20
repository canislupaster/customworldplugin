package com.thomas.customworld

import java.util.UUID

import com.thomas.customworld.rank.Rank
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin

import scala.collection.mutable

package object player {
  var pluginInstance:Plugin = _

  case class CustomWorldPlayer (permissionAttachment: PermissionAttachment, rank: Rank) {
    var rankPerms: Set[String] = Set()
    var perms: Set[String] = Set()
    var beforeTp: Option[Location] = None

    updateRank(rank)

    def updatePermissions (set: Set[String]): Unit = {
      perms foreach permissionAttachment.unsetPermission
      perms = set
      perms foreach (permissionAttachment.setPermission(_, true))
    }

    def updateRank (rank: Rank): Unit = {
      rankPerms foreach permissionAttachment.unsetPermission
      rankPerms = rank.permissions
      rankPerms foreach (permissionAttachment.setPermission(_,true))
    }
  }

  def initialize (plugin: Plugin): Unit = pluginInstance = plugin

  case class FreeOPPlayer (player: CustomWorldPlayer) extends CustomWorldPlayer (player.permissionAttachment, player.rank) {
    updatePermissions(Set("fawe.permpack.basic"))
  }

  case class MinigamePlayer (player: CustomWorldPlayer, inventory:Array[ItemStack]) extends CustomWorldPlayer (player.permissionAttachment, player.rank) {
    var playing:Boolean = false
  }

  var players: mutable.HashMap[UUID, CustomWorldPlayer] = mutable.HashMap()

  def getCustomPlayers[T <: CustomWorldPlayer]: collection.Map[UUID, T] = {
    players filter(_._2.isInstanceOf[T]) mapValues  (_.asInstanceOf[T])
  }

  def updateCustomPlayer (uUID: UUID, x:CustomWorldPlayer): Unit = players update(uUID, x)

  def getPlayers[T]: Array[Player] = {
    getCustomPlayers[T] map {case (x, _) => pluginInstance.getServer.getPlayer(x)} toArray
  }

  def doPlayers[T] (x: Player => Unit): Unit = {
    getPlayers[T] foreach x
  }

  def mapPlayers[T] (func: T => T): Unit = {
    getCustomPlayers[T] foreach {case (x,u) => players (x) (func(u))}
  }
}
