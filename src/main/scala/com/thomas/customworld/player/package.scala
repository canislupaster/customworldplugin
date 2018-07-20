package com.thomas.customworld

import java.util.UUID

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.rank.Rank
import org.bukkit.{GameMode, Location}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin
import com.thomas.customworld.CustomWorldPlugin._

import scala.collection.mutable

package object player {
  class CustomWorldPlayer (player: Player) {
    var rank:Rank = new PlayerDB(dbcons()).autoClose(db => db.getRank(player.getUniqueId))
    var permissionAttachment:PermissionAttachment = player.addAttachment(plugin)

    var rankPerms: Set[String] = Set()
    var perms: Set[String] = Set()
    var beforeTp: Option[Location] = None

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

    def deletePermissions(): Unit = {
      permissionAttachment.remove()
    }
  }

  case class FreeOPPlayer (player: Player) extends CustomWorldPlayer (player) {
    updatePermissions(Set("fawe.permpack.basic"))

  }

  case class MinigamePlayer (player: Player) extends CustomWorldPlayer (player)

  var players: mutable.HashMap[UUID, CustomWorldPlayer] = mutable.HashMap()

  def getCustomPlayers[T <: CustomWorldPlayer]: collection.Map[UUID, T] = {
    players filter(_._2.isInstanceOf[T]) mapValues  (_.asInstanceOf[T])
  }

  def updateCustomPlayer[T] (uUID: UUID, x:T => T): Unit = players (uUID) (x(getCustomPlayers[T](uUID)))

  def getPlayers[T]: Array[Player] = {
    getCustomPlayers[T] map {case (x, _) => plugin.getServer.getPlayer(x)} toArray
  }

  def doPlayers[T] (x: Player => Unit): Unit = {
    getPlayers[T] foreach x
  }

  def mapPlayers[T] (func: T => T): Unit = {
    getCustomPlayers[T] foreach {case (x,u) => players (x) (func(u))}
  }

  def getPermissions (player: Player): PermissionAttachment = player.addAttachment(plugin)

  def joinMinigames (player: Player): Unit = {

  }

  def joinFreeOP (player: Player): Unit = {
    players(player.getUniqueId) match {
      case x:MinigamePlayer => {
        x.deletePermissions()
      }
    }

    players(player.getUniqueId)(FreeOPPlayer)
    player.setGameMode(GameMode.CREATIVE)
  }
}
