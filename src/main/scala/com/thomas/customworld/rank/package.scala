package com.thomas.customworld

import java.util.UUID

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin

import scala.collection.mutable

package object rank {
  import scala.collection.mutable.HashMap
  var PlayerPerms: mutable.HashMap[UUID, PermissionAttachment] = mutable.HashMap[UUID, PermissionAttachment]()

  def Ranks = List(Regular, Helper, Builder, Mod, Staff, StaffPlus)

  def updateRank (player: Player, oldrank :Option[Rank], rank: Rank): Unit = {
    oldrank foreach (x => x.permissions foreach (PlayerPerms(player.getUniqueId).unsetPermission(_)))
    rank.permissions foreach (PlayerPerms(player.getUniqueId).setPermission(_, true))
  }

  def addPlayer (dbc:DBConstructor, player:Player, plugin: Plugin): Unit = {
    new PlayerDB (dbc()).autoClose(db => {
      PlayerPerms += player.getUniqueId -> player.addAttachment(plugin)
      updateRank(player, None, db.GetRank(player.getUniqueId))
    })
  }

  def removePlayer (dbc: DBConstructor, player: Player): Unit = {
    PlayerPerms -= player.getUniqueId
  }
}
