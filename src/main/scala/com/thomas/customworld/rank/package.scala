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

  def UpdateRank (player: Player, oldrank :Option[Rank], rank: Rank): Unit = {
    oldrank foreach (x => x.Permissions foreach (PlayerPerms(player.getUniqueId).unsetPermission(_)))
    rank.Permissions foreach (PlayerPerms(player.getUniqueId).setPermission(_, true))
  }

  def AddPlayer (dbc:DBConstructor, player:Player, plugin: Plugin): Unit = {
    new PlayerDB (dbc()).autoClose(db => {
      PlayerPerms += player.getUniqueId -> player.addAttachment(plugin)
      UpdateRank(player, None, db.GetRank(player.getUniqueId))
    })
  }

  def RemovePlayer (dbc: DBConstructor, player: Player): Unit = {
    PlayerPerms -= player.getUniqueId
  }
}
